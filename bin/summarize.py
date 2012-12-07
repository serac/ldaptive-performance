#!/usr/bin/env python

import numpy as np
import sys
from datetime import datetime
from os.path import basename

if len(sys.argv) < 2:
  print 'USAGE: %s path/to/output.csv' % basename(sys.argv[0])
  sys.exit()

f = open(sys.argv[1], 'r')
fail_count = 0
sample_times = []
fields = None
try:
  lines = f.readlines()
finally:
  f.close()
for line in lines:
  fields = line.split(',')
  start = int(fields[0])
  end = int(fields[1])
  sample_times.append(end - start)
  if fields[2].strip() != 'SUCCESS':
    fail_count += 1

print 'Median Tput (auths/sec): ', 1000 / np.median(sample_times)
print 'Std Dev (ms): ', np.std(sample_times)
print 'Failure Rate: ', fail_count * 100.0 / len(sample_times)
