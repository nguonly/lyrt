set title "Stacking Behavioral Invocation"
set key on left top vertical noreverse noenhanced autotitle nobox
set grid y
set auto x
#set nokey #remove legend
set boxwidth 1
set style fill pattern 1 border -1
#set style histogram clustered gap 1 #title textcolor lt -1
set style data histograms
#set xtics rotate by 45 right
plot 'lowering.csv' using 3:xtic(1) title "delegation" , '' using 2 title "lyrt"
