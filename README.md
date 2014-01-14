niah-sdc
========

** Needle in a Haystack (Niah) Algorithm ** 
* Assessing k-anonymity for Statistical Disclosure Control *

Copyright (C) 2012 Michael Comerford
comments/questions: michael@secretplan4b.co.uk

This program was developed as part of PhD Research at the University of Glasgow, and work carried out on internship with the Scottish Government.

Niah takes data formatted as comma separated values (csv) along with, a set of key variables (given by column number, starting from 0), and a threshold value k. Niah splits the file into 'atrisk' and 'safe' files based on an an assessment of k-anonymity (all records should be indistinguishable from k-1 other records).

This tool should be used as a part of a comprehensive statistical disclosure risk assessment. This should include: consideration of the data environment that data or statistical results are being published into; an assessment of how sensitive the data is, and the impact of any disclosure. These two aspects will assist in assigning an appropriate threshold level k.

As an example of k as defined above, if -th is set to 5 all combinations of the key variables with a record count of 4 or less will be deemed at risk.

Due to the memory requirements for processing large csv files, you may need to increase the maximum memory used by the Java Virtual Machine (use the -Xmx[value][unit] option, e.g. -Xmx512m) A general rule is that Niah requires the csv file x 10 available memory.


USAGE: Niah -kv "VAR1,...,VARx" [-th N] [-o OUTPUTNAME] FILE

OPTIONS: 
-kv "VAR1,...,VARx"        specify the column number of the key variables (comma separated) 
-th N                        (optional) specify the threshold for k-anonymity (defaults to 1)
-o OUTPUTNAME                (optional) specify the output file names (without extension)
FILE                        specify the filename for the input data (csv format required)
