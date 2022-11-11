# DNS database file for domain example.com
# It also includes a pointer to the primary server
# of the smaller.example.com subdomain
@ DEFAULT example.com.
TTL DEFAULT 86400
@ SOASP ns1.example.com. TTL
@ SOAADMIN dns\.admin.example.com. TTL
@ SOASERIAL 0117102022 TTL
@ SOAREFRESH 14400 TTL
@ SOARETRY 3600 TTL
@ SOAEXPIRE 604800 TTL
@ NS ns1.example.com. TTL
@ NS ns2.example.com. TTL
@ NS ns3.example.com. TTL
Smaller.@ NS sp.smaller.example.com.
@ MX mx1.example.com TTL 10
@ MX mx2.example.com TTL 20
ns1 A 193.136.130.250 TTL
ns2 A 193.137.100.250 TTL
ns3 A 193.136.130.251 TTL
sp.smaller A 193.140.90.11 TTL
mx1 A 193.136.130.200 TTL
mx2 A 193.136.130.201 TTL
www A 193.136.130.80 TTL 200
www A 193.136.130.81 TTL 200
ftp A 193.136.130.20 TTL
sp CNAME ns1 TTL
ss1 CNAME ns2 TTL
ss2 CNAME ns3 TTL
mail1 CNAME mx1 TTL
mail2 CNAME mx2 TTL 