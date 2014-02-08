

Mod_NoiseOsc {

  var server;
  var group;

  var synth;
  var <outBus, <ampModBus;

  *new { | amp = 0.1, relGroup = nil, addAction = 'addToTail' |
    ^super.new.prInit(amp, relGroup, addAction);
  }

  prInit { | amp = 0.1, relGroup = nil, addAction = 'addToTail' |
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDef;
      server.sync;
      this.prMakeGroup(relGroup, addAction);
      this.prMakeBusses;
      server.sync;
      this.prMakeSynth(amp);
    }
  }

  prAddSynthDef {
    SynthDef(\mod_noise, {
      |
      out, amp = 0.1, ampModIn, ampModFlag = 0,
      rangeLo = -1, rangeHi = 1
      |
      var noise, ampMod,  sig;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      noise = WhiteNoise.ar(1).range(rangeLo, rangeHi);
      sig = noise * ampMod;
      sig = sig * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;
  }

  prMakeGroup { | relGroup = nil, addAction = 'addToTail' |
    group = Group.new(relGroup, addAction);
  }

  prFreeGroup {
    group.free;
  }

  prMakeBusses {
    outBus = Bus.audio;
    ampModBus = Bus.audio;
  }

  prFreeBusses {
    outBus.free;
    ampModBus.free;
  }

  prMakeSynth { | amp = 0.1 |
    synth = Synth(\mod_noise, [\out, outBus, \ampModIn, ampModBus, \amp, amp], group, \addToTail);
  }

  prFreeSynth {
    synth.free;
  }

  //////// public functions:

  setAmp { | amp = 0.2 |
    synth.set(\amp, amp);
  }

  setVol { | vol = -12 |
    this.setAmp(vol.dbamp);
  }

  setFreq { | freq = 220 |
    synth.set(\freq, freq);
  }

  setFreqModIndex { | index = 50 |
    synth.set(\freqModIndex, index);
  }

  setPhaseModIndex { | index = 50 |
    synth.set(\phasModIndex, index);
  }

  setRangeLow { | rangeLow = -1 |
    synth.set(\rangeLo, rangeLow);
  }

  setRangeHigh { | rangeHigh = 1 |
    synth.set(\rangeHi, rangeHigh);
  }

  setRange { | rangeLow = -1, rangeHigh = 1 |
    this.setRangeLow(rangeLow);
    this.setRangeHigh(rangeHigh);
  }

  setAmpModFlag { | flag = 0 |
    synth.set(\ampModFlag, flag);
  }
}