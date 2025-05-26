In this project my goal is to deploy a Kubernetes cluster using Ansible. The cluster will be deployed on a set of virtual machines, and I will use Ansible playbooks to automate the installation and configuration of Kubernetes components.

## Prerequisites
- Ansible installed on your local machine.
- Access to a set of virtual machines (can be on a cloud provider or local VMs).
- SSH access to the virtual machines.
- Basic knowledge of Ansible and Kubernetes.

## Project Structure
```
./
├── ansible.cfg
├── inventory
├── playbooks
├── roles
├── group_vars
├── host_vars
├── requirements.yml
├── README.md