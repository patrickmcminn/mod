/*
Friday, February 7th
Mod_Output.sc
*/

Mod_Out {

  var server;
  var group;
  var <inBus, <panModBus, <ampModBus;
  var synth;

  *new { | outBus = 0, amp = 1, relGroup = nil, addAction = \addToTail |
    ^super.new.prInit(outBus, amp, relGroup, addAction);
  }

  prInit { | outBus = 0, amp = 1, relGroup = nil, addAction = \addToTail |
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDefs;
      server.sync;
      this.prMakeGroup(relGroup, addAction);
      this.prMakeBusses;
      server.sync;
      this.prMakeSynths(outBus, amp);
    }
  }

  prAddSynthDefs {

    SynthDef(\Mod_Output_Fader, {
      |
      inBus = 0, outBus = 0, amp = 1, pan = 0,
      panModInBus = 0, ampModInBus = 0, ampModFlag = 0
      |
      var input, filter, ampMod, panMod, panning, sig;
      input = InFeedback.ar(inBus);
      filter = HPF.ar(input, 5);
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModInBus)]);
      panMod = InFeedback.ar(panModInBus);
      panning = Pan2.ar(filter, pan + panMod);
      sig = panning * ampMod;
      sig = sig * amp;
      sig = sig.softclip;
      Out.ar(outBus, sig);
    }).add;

  }

  prMakeGroup { | relGroup = nil, addAction = \addToTail |
    group = Group.new(relGroup, addAction);
  }

  prFreeGroup { group.free; }

  prMakeBusses {
    inBus = Bus.audio;
    panModBus = Bus.audio;
    ampModBus = Bus.audio;
  }

  prFreeBusses {
    inBus.free;
    panModBus.free;
    ampModBus.free;
  }

  prMakeSynths { | outBus = 0, amp = 1 |
    synth = Synth(\Mod_Output_Fader, [\inBus, inBus, \outBus, outBus, \amp, amp,
      \panModInBus, panModBus, \ampModInBus, ampModBus], group, \addToTail);
  }

  prFreeSynths { synth.free; }

  /////// public functions:

  setAmp { | amp = 1 |
    synth.set(\amp, amp);
  }

  setVol { | vol = 0 |
    this.setAmp(vol.dbamp);
  }

  setPan { | pan = 0 |
    synth.set(\pan, pan);
  }

  setAmpModFlag { | flag = 0 |
    synth.set(\ampModFlag, flag);
  }



}