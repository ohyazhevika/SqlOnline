#!/usr/bin/env python3
"""$prolog"""

import argparse
import string
import datetime
import sys
import os.path
import glob
import struct
import binascii


READ_BLOCK_SIZE = 1024  # Should be the same as message buffer size
HEADER_CHECK_SIZE = 8  # Number of "message size + crc32" bytes
COMPONENTS_SEP = ','
# Total size + control sum
HEADER_CHECK_STRUCT = struct.Struct("=LL")
# flush buffer offset + time from the start of the epoch + length of file name string
HEADER_STRUCT_1 = struct.Struct("=QQL")
#
HEADER_STRUCT_2 = struct.Struct("=HBQLQL")

assert READ_BLOCK_SIZE % HEADER_CHECK_SIZE == 0

messages = []
args = None


def parse_args():
    global args
    parser = argparse.ArgumentParser(description='Converts trace binary files to human-readable format. When imported as module in gdb context, can read messages from the memory.',
                                     formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument('input_file', type=str,
                        help='file to convert', nargs="*")
    parser.add_argument('-s', '--start-time', type=str,
                        help='start time and date to filter messages in HH:MM:SS.us format with optional YYYY-MM-DD')
    parser.add_argument('-e', '--end-time', type=str,
                        help='end time and date to filter messages in HH:MM::SS.us format with optional YYYY-MM-DD')
    parser.add_argument('-c', '--components', type=str,
                        help='selects components to show')
    parser.add_argument('-f', '--format', type=str, help="""format string.
Following substitutions are allowed:
    %%date - date in YYYY-MM-DD format;
    %%time - time in HH:MM:SS.usec format;
    %%short_time - time in HH:MM:SS format;
    %%datetime - date and time in YYYY-MM-DD HH:MM:SS format; 
    %%usec - microseconds; 
    %%component - trace component name; 
    %%component_id - traced component id; 
    %%aspects - traced aspect names, separated by '+'; 
    %%aspect_ids - traced aspect ids, separated by '+'; 
    %%file - file where trace was called; 
    %%file_path - full path to file; 
    %%tid - task ID; 
    %%body - body of the trace message; 
    %%rawtime - time since the start of the epoch in microseconds;
    %%object_id - unique ID of a trace object;
NOTE you should explicitly include newline symbol (\\n) in format string.
    """)
    args = parser.parse_args()


def _from_null_terminated_str(block):
    chars = []
    for byte in block:
        ch = chr(byte)
        if ch == chr(0):
            return "".join(chars)
        chars.append(ch)


def _check_tail(block, size):
    """Checks bytes for an abrupt end"""
    return (len(block[:size]) == size)


class Message:
    _components = {
        0: {'name': 'test', 'aspects': {0: 'message', }},
        1: {'name': 'storage', 'aspects': {0: 'message', 1: "dump", 2: "scan", 3: "write", 4: "smo", 5: "checkpoint", 6: "dblog_dump", 7: "lock", 8: "pageio", 9: "stat"}},
        2: {'name': 'translate', 'aspects': {0: 'codegen', 1: 'codegen_supp', 2: 'pars', 3: 'link', 4: 'dict_cache', 5: 'pars_tree', 6: 'tables', 7:'dict_queries', 8:'scan', 9:'sort'}},
        3: {'name': 'core', 'aspects': {0: 'transaction'}},
        4: {'name': 'query_process', 'aspects': {0: 'sync'}},
        5: {'name': 'main', 'aspects': {0: 'connection', 1: 'transaction', 2: 'execution', 3: 'data'}},
    }

    def __init__(self):
        self.offset = 0
        self.time = 0
        self.file = ""
        self.line = -1
        self.component_id = -1
        self.aspect_mask = 0
        self.object_id = 0
        self.tid = -1
        self.body = ""
        self._full_length = 0
        self.crc32 = 0

    @property
    def component_name(self):
        return self._components[self.component_id]["name"]

    @property
    def aspects(self):
        selected_aspects = []
        id = 0
        mask = self.aspect_mask
        aspect_names = self._components[self.component_id]["aspects"]
        while mask:
            if (mask & 1):
                if id in aspect_names:
                    selected_aspects.append(aspect_names[id])
                else:
                    if not aspect_names:
                        selected_aspects.append(str(id))
            id += 1
            mask = mask >> 1
        return selected_aspects

    @property
    def aspect_ids(self):
        selected_aspects = []
        id = 0
        mask = self.aspect_mask
        while mask:
            if (mask & 1):
                selected_aspects.append(id)
            id += 1
            mask = mask >> 1
        return selected_aspects

    @property
    def datetime(self):
        return datetime.datetime.fromtimestamp(self.time/1000000)

    @property
    def datetime_usec(self):
        return self.time % 1000000

    @property
    def substitution(self):
        result = {
            "date": self.datetime.date(),
            "short_time": self.datetime.time().isoformat(timespec='seconds'),
            "time": self.datetime.time(),
            "datetime": self.datetime,
            "component_id": "{:02}".format(self.component_id),
            "component": self.component_name,
            "aspects": "+".join(self.aspects),
            "aspect_ids": "+".join(["{:02}".format(id) for id in self.aspect_ids]),
            "line": self.line,
            "file_path": self.file,
            "file": os.path.basename(self.file),
            "body": self.body,
            "tid": self.tid,
            "usec": "{:06}".format(self.datetime_usec),
            "rawtime": self.time,
            "object_id": self.object_id
        }
        return result

    @property
    def full_length(self):
        remainder = self._full_length % HEADER_CHECK_SIZE
        padding = HEADER_CHECK_SIZE - remainder if remainder else 0
        return self._full_length + padding

    @classmethod
    def from_bytes(cls, block, size):
        obj = cls()
        block = memoryview(block)
        index = 0

        # size of the message
        obj._full_length = size
        index += 4

        # next 4 bytes are checksum
        index += 4

        # flush buffer offset + time from the start of the epoch + length of file name string
        size = 8 + 8 + 4
        if not _check_tail(block[index:], size):
            return None
        obj.offset, obj.time, file_name_len = HEADER_STRUCT_1.unpack(
            block[index:index+size])
        index += size

        obj.file = block[index:index+file_name_len].tobytes().decode("utf-8")
        if len(obj.file) != file_name_len:
            return None
        index += len(obj.file)

        # line of the file + component id + aspect mask + task id + trace object id + message body size
        size = 2 + 1 + 8 + 4 + 8 + 4
        # length of the trace message
        if not _check_tail(block[index:], size):
            return None
        obj.line, obj.component_id, obj.aspect_mask, obj.tid, obj.object_id, message_body_len \
            = HEADER_STRUCT_2.unpack(block[index:index+size])
        index += size

        try:
            obj.body = block[index:index + message_body_len].tobytes().decode("utf-8")
        except UnicodeDecodeError:
            obj.body = "decode error!!!"
        index += message_body_len

        # message length do not include size and crc32
        assert index - HEADER_CHECK_SIZE == obj._full_length

        return obj

    def __str__(self):
        return "[{time}.{msec:06}]:{file}:{line}:{component}:{aspects}:{tid}:{object_id}:{body}".format(
            time=self.datetime.strftime("%Y-%m-%d %H:%M:%S"),
            msec=self.datetime_usec,
            file=self.file,
            line=self.line,
            component=self.component_name,
            aspects="+".join(self.aspects),
            tid=self.tid,
            object_id=self.object_id,
            body=self.body
        )


def _get_message_size(block):
    block = memoryview(block)
    full_length, crc32 = HEADER_CHECK_STRUCT.unpack(block[:HEADER_CHECK_SIZE])
    if full_length > READ_BLOCK_SIZE or full_length <= 0:
        # Message size cannot be more than the buffer size or zero size
        return 0
    checksum = binascii.crc32(
        block[HEADER_CHECK_SIZE:HEADER_CHECK_SIZE+full_length])
    if crc32 != checksum:
        return 0
    return full_length


def print_messages(messages, fmt=None):
    if fmt:
        fmt = fmt.replace("%", "$")
        fmt = fmt.replace("\\n", "\n")
    for message in messages:
        if fmt:
            template = string.Template(fmt)
            try:
                print(template.safe_substitute(message.substitution), end="")
            except BrokenPipeError:
                sys.exit(0)
        else:
            try:
                print(message)
            except BrokenPipeError:
                sys.exit(0)


def filter_message(message, args):
    result = False
    if args.components:
        components = args.components.split(COMPONENTS_SEP)
        if message.component_name not in components:
            result = True
    if args.start_time:
        try:
            start_time = datetime.datetime.strptime(
                args.start_time, "%H:%M:%S.%f").time()
            if message.datetime.time() < start_time:
                result = True
        except ValueError:
            start_time = datetime.datetime.strptime(
                args.start_time, "%Y-%m-%d %H:%M:%S.%f")
            if message.datetime < start_time:
                result = True
    if args.end_time:
        try:
            end_time = datetime.datetime.strptime(
                args.end_time, "%H:%M:%S.%f").time()
            if message.datetime.time() > end_time:
                result = True
        except ValueError:
            end_time = datetime.datetime.strptime(
                args.end_time, "%Y-%m-%d %H:%M:%S.%f")
            if message.datetime > end_time:
                result = True
    return result


def read_files(file_list):
    global args
    file_list.sort()
    block = bytearray()
    previous_msg_offset = 0
    previous_msg_time = 0
    message = None
    done = False
    for index, file in enumerate(file_list):
        input_file = open(file, 'rb')
        # byte block reading loop
        while True:
            block.extend(input_file.read(READ_BLOCK_SIZE - len(block)))
            if (len(block) < READ_BLOCK_SIZE) and (index + 1 < len(file_list)):
                # Need to read more from the next file
                break
            message_size = _get_message_size(block)
            if message_size == 0:
                block = block[HEADER_CHECK_SIZE:]
                # If there is no block left go to the next file
                if not block:
                    break
            else:
                message = Message.from_bytes(block, message_size)
                if message is not None:
                    block = block[HEADER_CHECK_SIZE + message.full_length:]
                    # Check if there are old or misplaced messages. If there are, stop reading.
                    if message.offset < previous_msg_offset:
                        done = True
                        break
                    previous_msg_offset = message.offset
                    previous_msg_time = message.time
                    if not filter_message(message, args):
                        print_messages([message], args.format)
                else:
                    break
        input_file.close()
        if done:
            break


if __name__ != "__main__":
    import gdb

    def __copy_buffer(circular_buffer, size):
        global HEADER_CHECK_SIZE
        block = bytearray()
        bytes_count = 4
        buf_t = gdb.lookup_type('uint32_t').pointer()
        circular_buffer = circular_buffer.cast(buf_t)  # We read each 4 bytes
        if size % bytes_count != 0:
            bytes_count = 1
        for _ in range(size//bytes_count):
            block += int(circular_buffer.dereference()
                         ).to_bytes(length=bytes_count, byteorder=sys.byteorder)
            circular_buffer += 1
        return block

    def _check_zeros(block):
        for byte in block:
            if byte != 0:
                return False
        return True

    def _read_msg_from_files(file_list, offset, max_file_size, block):
        global HEADER_CHECK_SIZE
        import os
        file_number = offset // max_file_size
        file_offset = offset % max_file_size
        file_name = file_list[file_number]
        input_file = open(file_name, 'rb')
        if file_offset > READ_BLOCK_SIZE:
            input_file.seek(file_offset - READ_BLOCK_SIZE)
            file_block = input_file.read(READ_BLOCK_SIZE)
        else:
            input_file.seek(0)
            file_block = input_file.read(file_offset)
            input_file = open(file_list[file_number-1], 'rb')
            file_offset = (offset - READ_BLOCK_SIZE) % max_file_size
            input_file.seek(file_offset)
            file_block = input_file.read() + file_block
        message_size = 0
        bytes_read_from_file = 0
        while not message_size:
            block = file_block[-HEADER_CHECK_SIZE:] + block
            file_block = file_block[:-HEADER_CHECK_SIZE]
            bytes_read_from_file += HEADER_CHECK_SIZE
            message_size = _get_message_size(block)
        message = Message.from_bytes(block, message_size)
        return message.full_length - bytes_read_from_file, message

    def load_from_gdb_context(count=None):
        global HEADER_CHECK_SIZE, READ_BLOCK_SIZE
        tracer = gdb.parse_and_eval('__tracer')
        flush_buffer = tracer["flush_task_ctx"]["buf"]
        # We operate with single bytes
        buf_t = gdb.lookup_type('uint8_t').pointer()
        circular_buffer = flush_buffer["buffer"].cast(buf_t)
        buffer_size = int(flush_buffer["buffer_size"])
        max_file_size = int(tracer["ctx"]["file_size"])
        allocated = int(flush_buffer["allocated"])
        flushed = int(flush_buffer["flushed"])
        initial_offset = flushed % buffer_size
        read_max = allocated - flushed
        assert read_max < buffer_size
        position = circular_buffer + initial_offset
        buffer_end = circular_buffer + buffer_size
        bytes_read = 0
        messages_read = 0
        block = bytearray()
        while bytes_read < read_max:
            new_position = position + (READ_BLOCK_SIZE - len(block))
            if new_position < buffer_end:
                buffer_part = __copy_buffer(
                    position, READ_BLOCK_SIZE - len(block))
            else:
                buffer_part = __copy_buffer(
                    position, int(buffer_end - position))
            block += buffer_part
            if new_position >= buffer_end:
                # Return to the beginning
                position = circular_buffer
                continue
            position = new_position
            if _check_zeros(block):
                break
            message_size = _get_message_size(block)
            if message_size:
                if message_size < len(block):
                    message = Message.from_bytes(block, message_size)
                    print_messages([message])
                    messages_read += 1
                    if (count is not None) and (messages_read >= count):
                        return
                    bytes_read += HEADER_CHECK_SIZE + message.full_length
                    block = block[HEADER_CHECK_SIZE + message.full_length:]
            else:
                if bytes_read == 0:
                    prefix = str(tracer["farr"]["name"].string())
                    template = "{}_*.dat".format(prefix)
                    file_list = glob.glob(template)
                    if not file_list:
                        print("ERROR: trace files are absent. ")
                        return
                    file_list.sort()
                    bytes_read, message = _read_msg_from_files(
                        file_list, flushed, max_file_size, block)
                    print_messages([message])
                    messages_read += 1
                else:
                    bytes_read += HEADER_CHECK_SIZE
                    block = block[HEADER_CHECK_SIZE:]

    class TraceMessages(gdb.Command):
        """Prints trace messages"""

        def __init__(self):
            super(TraceMessages, self).__init__("msg", gdb.COMMAND_STATUS)

        def invoke(self, arg, from_tty):
            import traceback
            try:
                self._invoke(arg, from_tty)
            except Exception:
                traceback.print_exc()

        def _invoke(self, arg, from_tty):
            num = None
            try:
                num = int(arg)
            except ValueError:
                pass
            load_from_gdb_context(num)

    TraceMessages()

if __name__ == "__main__":
    parse_args()
    if type(args.input_file) is not list:
        file_list = glob.glob(args.input_file)
    else:
        file_list = args.input_file
    read_files(file_list)
    print()
