host=masum@74.225.185.84

ssh $host python3 --version

# install pip3 if not installed
# ssh $host sudo apt-get install -y python3-pip
# ssh $host pip3 install --upgrade pip
# ssh $host pip3 install ansible
# # refresh bashrc to ensure ansible is available
# ssh $host source ~/.bashrc
# # check ansible versions
ssh $host ansible --version

rsync -avz k8s-ansible $host:

ssh $host ls -l /home/masum/k8s-ansible