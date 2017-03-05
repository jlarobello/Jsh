# Jsh
A Java based shell that provides functions of a real shell
## Prerequsites
Can only be run on unix-like os. Install latest [make](https://www.gnu.org/software/make/) and at least [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) before running.
### Running on VM via Vagrant
Ensure [VirtualBox](https://www.virtualbox.org/wiki/VirtualBox) and [Vagrant](https://www.vagrantup.com/docs/getting-started/) installed before running Vagrantfile. cd into repo. Then run
```
vagrant up
vagrant provision
vagrant ssh
cd /vagrant/JavaShell
```
## Build and Run
Before building, cd into the JavaShell directory. After, run the following command to build and run the shell
```
make run
```
Additonaly, run Jsh with
```
java Jsh
```
## Built With
[Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - general purpose programming language

[Vagrant](https://www.vagrantup.com/) - portable development environment manager

[VirtualBox](https://www.virtualbox.org/wiki/VirtualBox) - general-purpose vmm
