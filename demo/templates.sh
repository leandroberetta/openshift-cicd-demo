#! /usr/bin/env bash

#
# Author: Leandro Beretta <lberetta@redhat.com>
#
# Script to create the templates used by the demo.
#

#
# Templates needed
#

oc login -u system:admin

oc create -f jboss-image-streams.json -n openshift
oc create -f eap70-basic-s2i.json -n openshift
