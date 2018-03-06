
set title "Method Invocation"
set grid y
set nokey #remove legend
set boxwidth 0.5
set style fill solid
set xtics rotate by 45 right
plot 'invocation.csv' using 1:3:xtic(2) with boxes, '' using 1:3:3 with labels offset char 0, 1
