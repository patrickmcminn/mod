

Mod_SinOsc {

  var server;
  var group;

  var synth;
  var <outBus, <freqModBus, <ampModBus, <phaseModBus;

  *new { | freq = 220, amp = 0.2, relGroup = nil, addAction = 'addToTail' |
    ^super.new.prInit(freq, amp, relGroup, addAction);
  }

  prInit { | freq = 220, amp = 0.2, relGroup = nil, addAction = 'addToTail' |
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDef;
      server.sync;
      this.prMakeGroup(relGroup, addAction);
      this.prMakeBusses;
      server.sync;
      this.prMakeSynth(freq, amp);
    }
  }

  prAddSynthDef {
    SynthDef(\mod_sine, {
      |
      out = nil, amp = 0.2, freq = 220,
      freqModIn, freqModIndex = 50, phaseModIn, phaseModIndex = 1
      ampModFlag = 0, ampModIn,
      rangeLo = -1, rangeHi = 1
      |
      var freqMod, phaseMod, ampMod, sine, sig;
      freqMod = InFeedback.ar(freqModIn) * freqModIndex;
      phaseMod = InFeedback.ar(phaseModIn) * phaseModIndex;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      sine = SinOsc.ar((freq + freqMod), phaseMod).range(rangeLo, rangeHi);
      sig = sine * ampMod;
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
    freqModBus = Bus.audio;
    ampModBus = Bus.audio;
    phaseModBus = Bus.audio;
  }

  prFreeBusses {
    outBus.free;
    freqModBus.free;
    ampModBus.free;
    phaseModBus.free;
  }

  prMakeSynth { | freq = 220, amp = 0.2 |
    synth = Synth(\mod_sine, [\out, outBus, \freq, freq, \freqModIn, freqModBus, \phaseModIn, phaseModBus,
      \ampModIn, ampModBus, \amp, amp], group, \addToTail);
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