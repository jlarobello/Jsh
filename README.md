# Jsh
A Java based shell that provides functions of a real shell
## Prerequsites
[Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
### Running on VM via Vagrant
Install VirtualBox and Vagrant and execute the following before running
```
vagrant up
vagrant provision
vagrant ssh
cd /vagrant/JavaShell
```
## Build and Run
```
make
java Jsh
```
## Built With
[Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - general purpose programming language

[Vagrant](https://www.vagrantup.com/) - portable development environment manager

[VirtualBox](https://www.virtualbox.org/wiki/VirtualBox) - general-purpose vmm
