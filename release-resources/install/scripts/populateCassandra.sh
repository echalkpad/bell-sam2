#!/bin/sh

echo `cqlsh -f /bell_sam_admin_service.cql`
echo `cqlsh -f /bell_sam_cassandra.cql`

echo `cqlsh -f ~/opt/workspaces/bell-sam/admin-service/src/main/resources/cassandraScripts/bell_sam_admin_service.cql`
echo `cqlsh -f /bell_sam_cassandra.cql`

