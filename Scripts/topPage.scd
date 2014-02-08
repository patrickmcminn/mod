/*
Friday, February 7th 2014
topPage.scd
prm
*/

var page = 'modTop';

try { if( ~ohm.isNil, { ~ohm = OhmRGB.new; }); };

~ohm.makePage(page);

~ohm.turnGridBlue(0, 0, page: page);
