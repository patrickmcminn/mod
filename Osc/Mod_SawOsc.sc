Mod_SawOsc {

  var server;
  var group;

  var synth;
  var <outBus, <freqModBus, <ampModBus;

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
    SynthDef(\mod_saw, {
      |
      out, amp = 0.2, freq = 220, freqModIn, freqModIndex = 1,
      ampModIn, ampModFlag = 0,
      rangeLo = -1, rangeHi = 1
      |
      var freqMod, ampMod, saw, filter, sig;
      freqMod = InFeedback.ar(freqModIn) * freqModIndex;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      saw = LFSaw.ar(freq + freqMod).range(rangeLo, rangeHi);
      filter = LPF.ar(saw, 20000);
      sig = filter * ampMod;
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
  }

  prFreeBusses {
    outBus.free;
    freqModBus.free;
    ampModBus.free;
  }

  prMakeSynth { | freq = 220, amp = 0.2 |
    synth = Synth(\mod_saw, [\out, outBus, \freq, freq, \freqModIn, freqModBus, \ampModIn, ampModBus, \amp, amp],
      group, \addToTail);
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