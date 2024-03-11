FROM ubuntu:22.04

WORKDIR ./binaries
COPY . .
RUN apt-get update -y
RUN apt-get install -y libnuma-dev
EXPOSE 2061
CMD ["soqol/bin/vsql_server"]