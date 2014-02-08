/*
Saturday, February 8th 2014
startupFunc.scd
prm
*/

~mod = Mod.new;

~mod.addOutModule(Mod_Out.new, 'out');
~mod.addOscModule(Mod_Osc.new, 'osc1');
