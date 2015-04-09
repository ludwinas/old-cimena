# -*- mode: ruby -*-
Vagrant.configure(2) do |config|
    # which box should we start from
    config.vm.box = "ubuntu/trusty64"
    # port forwards
    config.vm.network "forwarded_port", guest: 3000, host: 3000
    # ansible configuration
    config.vm.provision "ansible" do |ansible|
        ansible.playbook = "cimena/ansible/playbook.yml"
    end
    # configure virtualbox to have more ram and 2 cpus
    config.vm.provider "virtualbox" do |v|
        v.memory = 2048
        v.cpus = 2
    end
end
