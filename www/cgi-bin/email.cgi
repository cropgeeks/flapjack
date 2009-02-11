#!/usr/bin/perl

use CGI;
use strict;

print "Content-type: text/html\n\n";

# Get CGI query variables
my $cgi_query = CGI->new();
my $cmd         = $cgi_query->param("cmd");
my $email       = $cgi_query->param("email");  
my $institution = $cgi_query->param("institution");  

my $date = `date`;
chomp $date;


open (LOG, ">>/var/www/html/flapjack/logs/email.log");
print LOG "$date\t$cmd\t$email\t$institution\r\n";

close LOG;
