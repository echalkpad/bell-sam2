CREATE KEYSPACE IF NOT EXISTS bell_sam WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'} AND durable_writes = true;

USE bell_sam;

CREATE TABLE IF NOT EXISTS bell_sam.allowed_services (allowed_service_id text PRIMARY KEY, application_ids set<uuid>, created_timestamp timestamp, last_modified_timestamp timestamp );

CREATE TABLE IF NOT EXISTS bell_sam.applications (application_id uuid PRIMARY KEY, client_id text, role_ids set<text>, policies set<text>, gateway_policies set<text>, allowed_service_ids set<text>, name text, email text, secret text, account_state text, created_timestamp timestamp, last_modified_timestamp timestamp );

CREATE TABLE IF NOT EXISTS bell_sam.clients (client_id text PRIMARY KEY, application_id uuid, created_timestamp timestamp, last_modified_timestamp timestamp );

CREATE TABLE IF NOT EXISTS bell_sam.policy_types (policy_type_id text PRIMARY KEY, name text, policy_order int, service_type_ids set<text> );

CREATE TABLE IF NOT EXISTS bell_sam.protocol_types (protocol_type_Id text PRIMARY KEY, description text, service_type_ids set<text> );

CREATE TABLE IF NOT EXISTS bell_sam.roles (role_id text PRIMARY KEY, description text, service_type_ids set<text>, application_ids set<uuid> );

CREATE TABLE IF NOT EXISTS bell_sam.service_types (service_type_id text PRIMARY KEY, name text, policy_type_ids set<text>, protocol_type_id text );
