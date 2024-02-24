#!/bin/bash

### Kubernetes Secret
# Create a Kubernetes secret with the queue manager key and certificate
oc delete secret example-qm-tls -n cp4i
