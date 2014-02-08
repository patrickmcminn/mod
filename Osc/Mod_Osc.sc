/*
Saturday, October 19th 2013
Mod_Osc.sc
prm
*/

Mod_Osc {

  var server;
  var group;
  var <sine, <tri, <square, <saw, <noise;
  var <sineOutBus, <triOutBus, <squareOutBus, <sawOutBus, <noiseOutBus, <nilBus;
  var <sineFreqModBus, <sinePhaseModBus, <sineAmpModBus;
  var <triFreqModBus, <triAmpModBus;
  var <squareFreqModBus, <squareWidthModBus, <squareAmpModBus;
  var <sawFreqModBus, <sawAmpModBus;
  var <noiseAmpModBus;

  var <masterFreq, <sineFreq, <triFreq, <squareFreq, <sawFreq;
  var <masterAmp, <sineAmp, <triAmp, <squareAmp, <sawAmp, <noiseAmp;

  *new { | freq = 220, relGroup = nil, addAction = 'addToHead' |
    ^super.new.prInit(freq, relGroup, addAction);
  }

  prInit { | freq = 220, relGroup = nil, addAction = 'addToHead' |
    server = Server.default;
    server.waitForBoot {
      this.prAddSynthDefs;
      server.sync;
      this.prMakeBusses;
      this.prMakeGroup(relGroup, addAction);
      server.sync;
      this.prMakeSynths(freq);
    };
  }

  free {
    this.prFreeSynths;
    this.prFreeBusses;
    this.freeGroup;
  }

  prAddSynthDefs {

    SynthDef(\mod_sine, {
      |
      out = nil, amp = 0.2, freq = 220,
      freqModIn, freqModIndex = 1,  phaseModIn, phaseModIndex = 1
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


    SynthDef(\mod_tri, {
      |
      out, amp = 0.2, freq = 220,
      freqModIn, freqModIndex = 1, ampModIn, ampModFlag = 0,
      rangeLo = -1, rangeHi = 1
      |
      var freqMod, ampMod, tri, sig;
      freqMod = InFeedback.ar(freqModIn) * freqModIndex;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      tri = Tri.ar(freq + freqMod).range(rangeLo, rangeHi);
      sig = tri * ampMod;
      sig = sig * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;


    SynthDef(\mod_square, {
      |
      out, amp = 0.2, freq = 220,
      width = 0.5, widthModIn, widthModFlag = 0,
      freqModIn, freqModIndex = 1,
      ampModIn, ampModFlag = 0,
      rangeLo = -1, rangeHi = 1
      |
      var widthMod, freqMod, ampMod, square, sig;
      widthMod = Select.ar(widthModFlag, [DC.ar(1), InFeedback.ar(widthModIn)]);
      freqMod = InFeedback.ar(freqModIn) * freqModIndex;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      square = Pulse.ar(freq + freqMod, width * widthMod);
      sig = square * ampMod;
      sig = sig * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

     SynthDef(\mod_saw, {
      |
      out, amp = 0.2, freq = 220, freqModIn, freqModIndex = 1,
      ampModIn, ampModFlag = 0,
      rangeLo = -1, rangeHi = 1
      |
      var freqMod, ampMod, saw, sig;
      freqMod = InFeedback.ar(freqModIn) * freqModIndex;
      ampMod = Select.ar(ampModFlag, [DC.ar(1), InFeedback.ar(ampModIn)]);
      saw = Saw.ar(freq + freqMod).range(rangeLo, rangeHi);
      sig = saw * ampMod;
      sig = sig * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

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

  prMakeGroup { | relGroup = nil, addAction = 'addToHead' |
    group = Group.new(relGroup, addAction);
  }

  prMakeBusses {
    sineOutBus = Bus.audio;
    sineFreqModBus = Bus.audio;
    sineAmpModBus = Bus.audio;
    sinePhaseModBus = Bus.audio;

    triOutBus = Bus.audio;
    triFreqModBus = Bus.audio;
    triAmpModBus = Bus.audio;

    squareOutBus = Bus.audio;
    squareFreqModBus = Bus.audio;
    squareWidthModBus = Bus.audio;
    squareAmpModBus = Bus.audio;

    sawOutBus = Bus.audio;
    sawFreqModBus = Bus.audio;
    sawAmpModBus = Bus.audio;

    noiseOutBus = Bus.audio;
    noiseAmpModBus = Bus.audio;

    //nilBus = Bus.audio;

  }

  prFreeBusses {
    sineOutBus.free;
    sineFreqModBus.free;
    sineAmpModBus.free;
    sinePhaseModBus.free;

    triOutBus.free;
    triFreqModBus.free;
    triAmpModBus.free;

    squareOutBus.free;
    squareFreqModBus.free;
    squareWidthModBus.free;
    squareAmpModBus.free;

    sawOutBus.free;
    sawFreqModBus.free;
    sawAmpModBus.free;

    noiseOutBus.free;
    noiseAmpModBus.free;

    //nilBus.free;
  }


  prMakeSynths { | freq = 220 |
    masterFreq = freq;
    sine = Synth(\mod_sine, [\out, sineOutBus, \freq, masterFreq, \freqModIn, sineFreqModBus, \phaseModIn, sinePhaseModBus,
      \ampModIn, sineAmpModBus], group, \addToTail);
    tri = Synth(\mod_tri, [\out, triOutBus, \freq, masterFreq, \freqModIn, triFreqModBus, \ampModIn, triAmpModBus],
      group, \addToTail);
    square = Synth(\mod_square, [\out, squareOutBus, \freq, masterFreq, \freqModIn, squareFreqModBus, \widthModIn, squareWidthModBus,
      \ampModIn, squareAmpModBus], group, \addToTail);
    saw = Synth(\mod_saw, [\out, sawOutBus, \freq, masterFreq, \freqModIn, sawFreqModBus, \ampModIn, sawAmpModBus],
      group, \addToTail);
    noise = Synth(\mod_noise, [\out, noiseOutBus, \ampModIn, noiseAmpModBus], group, \addToTail);
  }

  prFreeSynths {
    sine.free;
    tri.free;
    square.free;
    saw.free;
    noise.free;
  }

  //////// Public Functions

  setMasterFreq { | freq |
    masterFreq = freq;
    sineFreq = masterFreq;
    triFreq = masterFreq;
    squareFreq = masterFreq;
    sawFreq = masterFreq;
    sine.set(\freq, sineFreq);
    tri.set(\freq, triFreq);
    square.set(\freq, squareFreq);
    saw.set(\freq, sawFreq);
  }

  resetMasterFreq {
    sineFreq = masterFreq;
    triFreq = masterFreq;
    squareFreq = masterFreq;
    sawFreq = masterFreq;
    sine.set(\freq, sineFreq);
    tri.set(\freq, triFreq);
    square.set(\freq, squareFreq);
    saw.set(\freq, sawFreq);
    ^masterFreq;
  }

  setFreq { | freq = 220, osc = 'sine' |
    switch(osc,
      { \sine }, { this.setSineFreq(freq); },
      { \tri } , { this.setTriFreq(freq); },
      { \square }, { this.setSquareFreq(freq); },
      { \saw }, { this.setSawFreq(freq); },
    );
  }

  setSineFreq { | freq |
    sineFreq = freq;
    sine.set(\freq, sineFreq);

  }

  setTriFreq { | freq |
    triFreq = freq;
    tri.set(\freq, triFreq);
  }

  setSquareFreq { | freq |
    squareFreq = freq;
    square.set(\freq, squareFreq);
  }

  setSawFreq { | freq |
    sawFreq = freq;
    saw.set(\freq, sawFreq);
  }

  setMasterAmp { | amp |
    masterAmp = amp;
    sineAmp = masterAmp;
    triAmp = masterAmp;
    squareAmp = masterAmp;
    sawAmp = masterAmp;
    noiseAmp = masterAmp;
    sine.set(\amp, sineAmp);
    tri.set(\amp, triAmp);
    square.set(\amp, squareAmp);
    saw.set(\amp, sawAmp);
    noise.set(\amp, noiseAmp);
  }

  resetMasterAmp {
    sineAmp = masterAmp;
    triAmp = masterAmp;
    squareAmp = masterAmp;
    sawAmp = masterAmp;
    noiseAmp = masterAmp;
    sine.set(\amp, sineAmp);
    tri.set(\amp, triAmp);
    square.set(\amp, squareAmp);
    saw.set(\amp, sawAmp);
    noise.set(\amp, noiseAmp);
    ^masterAmp;
  }

  setAmp { | amp, osc = 'sine ' |
    switch(osc,
      { 'sine' }, { this.setSineAmp(amp); },
      { 'tri' }, { this.setTriAmp(amp); },
      { 'square' }, { this.setSquareAmp(amp); },
      { 'saw' }, { this.setSawAmp(amp); },
      { 'noise' }, { this.setNoiseAmp(amp); }
    );
  }

  setSineAmp { | amp |
    sineAmp = amp;
    sine.set(\amp, sineAmp);
  }

  setTriAmp { | amp |
    triAmp = amp;
    tri.set(\amp, triAmp);
  }

  setSquareAmp { | amp |
    squareAmp = amp;
    square.set(\amp, squareAmp);
  }

  setSawAmp { | amp |
    sawAmp = amp;
    saw.set(\amp, sawAmp);
  }

  setNoiseAmp { | amp |
    noiseAmp = amp;
  }

  setFreqModIndex { | index = 1, osc = 'sine' |
    switch(osc,
      { 'sine' }, { sine.set(\freqModIndex, index); },
      { 'tri' }, { tri.set(\freqModIndex, index); },
      { 'square' }, { square.set(\freqModIndex, index) },
      { 'saw' }, { saw.set(\freqModIndex, index) }
    );
  }

  setSineFreqModIndex { | index | this.setFreqModIndex(index, 'sine'); }

  setTriFreqModIndex { | index | this.setFreqModIndex(index, 'tri'); }

  setSquareFreqModIndex { | index | this.setFreqModIndex(index, 'square'); }

  setSawFreqModIndex { | index | this.setFreqModIndex(index, 'saw'); }

  setAmpModFlag { | flag = 0, osc = 'sine' |
    switch(osc,
      { 'sine' }, { sine.set(\ampModFlag, flag); },
      { 'tri' }, { tri.set(\ampModFlag, flag); },
      { 'square' }, { square.set(\ampModFlag, flag); },
      { 'saw' }, { saw.set(\ampModFlag, flag); },
      { 'noise' }, { noise.set(\ampModFlag, flag); }
    );
  }

  setSineAmpModFlag { | flag = 0 | this.setAmpModFlag(flag, 'sine'); }

  setTriAmpModFlag { | flag = 0 | this.setAmpModFlag(flag, 'tri'); }

  setSquareAmpModFlag { | flag = 0 | this.setAmpModFlag(flag, 'square'); }

  setSawAmpModFlag { | flag = 0 | this.setAmpModFlag(flag, 'saw'); }

  setNoiseAmpModFlag { | flag = 0 | this.setAmpModFlag(flag, 'noise'); }

  setSinePhaseModIndex { | index = 1 | sine.set(\phaseModIndex, index) }

  setSquareWidthModFlag { | flag = 0 | square.set(\widthModFlag, flag); }

  setRangeLow { | rangeLow = -1, osc = 'sine' |
    switch(osc,
      { 'sine' }, { sine.set(\rangeLo, rangeLow); },
      { 'tri' }, { tri.set(\rangeLo, rangeLow); },
      { 'square' }, { square.set(\rangeLo, rangeLow); },
      { 'saw' }, { saw.set(\rangeLo, rangeLow); },
      { 'noise' }, { noise.set(\rangeLo, rangeLow); }
    );
  }

  setSineRangeLow { | rangeLow = -1 | this.setRangeLow(rangeLow, 'sine'); }

  setTriRangeLow { | rangeLow = -1 | this.setRangeLow(rangeLow, 'tri'); }

  setSquareRangeLow { | rangeLow = -1 | this.setRangeLow(rangeLow, 'square'); }

  setSawRangeLow { | rangeLow = -1 | this.setRangeLow(rangeLow, 'saw'); }

  setNoiseRangeLow { | rangeLow = -1 | this.setRangeLow(rangeLow, 'noise'); }

  setRangeHigh { | rangeHigh = 1, osc = 'sine' |
    switch(osc,
      { 'sine' }, { sine.set(\rangeHi, rangeHigh); },
      { 'tri' }, { tri.set(\rangeHi, rangeHigh); },
      { 'square' }, { square.set(\rangeHi, rangeHigh); },
      { 'saw' }, { saw.set(\rangeHi, rangeHigh); },
      { 'noise' }, { noise.set(\rangeHi, rangeHigh); }
    );
  }

  setSineRangeHigh { | rangeHigh = 1 | this.setRangeHigh(rangeHigh, 'sine'); }

  setTriRangeHigh { | rangeHigh = 1 | this.setRangeHigh(rangeHigh, 'tri'); }

  setSquareRangeHigh { | rangeHigh = 1 | this.setRangeHigh(rangeHigh, 'square'); }

  setSawRangeHigh { | rangeHigh = 1 | this.setRangeHigh(rangeHigh, 'saw'); }

  setNoiseRangeHigh { | rangeHigh = 1 | this.setRangeHigh(rangeHigh, 'noise'); }

  setRange { | rangeLow = -1, rangeHigh = 1, osc = 'sine' |
    switch(osc,
      { 'sine' }, { this.setSineRange(rangeLow, rangeHigh); },
      { 'tri' }, { this.setTriRange(rangeLow, rangeHigh); },
      { 'square' }, { this.setSquareRange(rangeLow, rangeHigh); },
      { 'saw' }, { this.setSawRange(rangeLow, rangeHigh); },
      { 'noise' }, { this.setNoiseRange(rangeLow, rangeHigh); }
    );
  }

  setSineRange { | rangeLow = -1, rangeHigh = 1 |
    this.setSineRangeLow(rangeLow);
    this.setSineRangeHigh(rangeHigh);
  }

  setTriRange { | rangeLow = -1, rangeHigh = 1 |
    this.setTriRangeLow(rangeLow);
    this.setTriRangeHigh(rangeHigh);
  }

  setSquareRange { | rangeLow = -1, rangeHigh = 1 |
    this.setSquareRangeLow(rangeLow);
    this.setSquareRangeHigh(rangeHigh);
  }

  setSawRange { | rangeLow = -1, rangeHigh = 1 |
    this.setSawRangeLow(rangeLow);
    this.setSawRangeHigh(rangeHigh);
  }

  setNoiseRange { | rangeLow = -1, rangeHigh = 1 |
    this.setNoiseRangeLow(rangeLow);
    this.setNoiseRangeHigh(rangeHigh);
  }

}