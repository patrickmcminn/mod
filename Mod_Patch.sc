/*
Thursday, February 6th 2013
Mod_Patch.sc
*/


Mod_Patch {
  var server;
  var synth;

  *new { |outBus = 0, inBus = 1, group = nil, addAction = \addAfter|
    ^super.new.prInit(outBus, inBus, group, addAction);
  }

  prInit { |outBus = 0, inBus = 1, group = nil, addAction = \addAfter|
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDef;
      server.sync;
      this.prMakeSynth(outBus, inBus, group, addAction);
    };
  }

  prAddSynthDef {
    SynthDef(\Mod_Patch, { |inBus = 0, outBus = 0|
      var sig = InFeedback.ar(inBus, 1);
      Out.ar(outBus, sig);
    }).add;

  }

  prMakeSynth { |outBus = 0, inBus = 1, group = nil, addAction = \addAfter|
    synth = Synth(\Mod_Patch, [\outBus, outBus, \inBus, inBus], group, addAction);
  }

  free {
    synth.free;
    server = nil;
  }
}