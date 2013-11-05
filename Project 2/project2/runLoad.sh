#!/bin/bash

# Inside the drop.sql, you sould check whether the table exists. Drop them ONLY if they exists.
mysql CS144 < drop.sql

# Run the create.sql batch file to create the database and tables
mysql CS144 < create.sql

# Compile and run the parser to generate the appropriate load files
ant 
ant -f build.xml run-all

# Run the load.sql batch file to load the data
mysql CS144 < load.sql

# Delete temporary .dat files
rm *.dat 