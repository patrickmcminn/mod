/*
Monday, October 21st 2013
Mod_Filter.sc
prm
*/

Mod_Filter {

  var server, group;

  var lowPass, highPass, bandPass;

  var <lowPassInBus, <highPassInBus, <bandPassInBus;
  var <lowPassOutBus, <highPassOutBus, <bandPassOutBus, <nilBus;
  var lowPassCutoffModBus, highPassCutoffModBus, bandPassCutoffModBus;
  var lowPassResModBus, highPassResModBus, bandPassResModBus;

  var masterCutoff, masterRes, masterAmp;
  var lowPassCutoff, highPassCutoff, bandPassCenterFreq;
  var lowPassRes, highPassRes, bandPassRes;
  var lowPassAmp, highPassAmp, bandPassAmp;

  *new { | cutoff, res, amp |
    ^super.new.prInit(cutoff, res, amp);
  }

  prInit { | cutoff, res, amp |
    server = Server.default;
    server.waitForBoot {
      this.prMakeSynthDefs;
      server.sync;
      this.prMakeBusses;
      server.sync;
      this.prMakeSynths(cutoff, res, amp);
    };
  }

  free {
    this.prFreeSynths;
    this.prFreeBusses;
  }

  prMakeSynthDefs {

    SynthDef(\mod_lowPass, {
      |
      in, out, amp = 1, cutoff = 1000, res = 0,
      cutoffModIn, cutoffModGain = 1, cutoffModOffset = 0,
      resModIn, resModGain = 0.3, resModOffset = 0,
      noise = 0.0003
      |
      var input, cutoffMod, resMod, filter, sig;
      input = InFeedback.ar(in);
      cutoffMod = InFeedback.ar(cutoffModIn) * cutoffModGain;
      cutoffMod = (cutoffMod.linlin(-1.0, 1.0, (cutoff * 0.75).neg, cutoff * 0.75, \min)) + cutoffModOffset;
      cutoffMod.poll;
      resMod = InFeedback.ar(resModIn) * resModGain;
      resMod = resMod.linlin(-1.0, 1.0, 0.0, 1.0, \min) + resModOffset;
      filter = DFM1.ar(input, cutoff + cutoffMod, res + resMod, 1, 0, noise);
      sig = filter * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

    SynthDef(\mod_highPass, {
      |
      in, out, amp = 1, cutoff = 1000, res = 0,
      cutoffModIn, cutoffModGain = 1, cutoffModOffset = 0,
      resModIn, resModGain = 0.3, resModOffset = 0,
      noise = 0.0003
      |
      var input, cutoffMod, resMod, filter, sig;
      input = InFeedback.ar(in);
      cutoffMod = InFeedback.ar(cutoffModIn) * cutoffModGain;
      cutoffMod = (cutoffMod.linlin(-1.0, 1.0, (cutoff * 0.75).neg, cutoff * 0.75, \min)) + cutoffModOffset;
      resMod = InFeedback.ar(resModIn) * resModGain;
      resMod = resMod.linlin(-1.0, 1.0, 0.0, 1.0, \min) + resModOffset;
      filter = DFM1.ar(input, cutoff + cutoffMod, res + resMod, 1, 1, noise);
      sig = filter * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

    SynthDef(\mod_bandPass, {
      |
      in, out, amp = 1, centerFreq = 1000, res = 1,
      centerFreqModIn, centerFreqModGain = 1, centerFreqModOffset = 0,
      resModIn, resModGain = 1, resModOffset = 0
      |
      var input, centerFreqMod, resMod, filter, sig;
      input = InFeedback.ar(in);
      centerFreqMod = InFeedback.ar(centerFreqModIn) * centerFreqModGain;
      centerFreqMod = centerFreqMod.linlin(-1.0, 1.0, (centerFreq * 0.75).neg, centerFreq * 0.75, \min) + centerFreqModOffset;
      //centerFreqMod = InFeedback.ar(centerFreqModIn).range(centerFreqModLo, centerFreqModHi);
      resMod = InFeedback.ar(resModIn) * resModGain;
      resMod = resMod.linlin(-1.0, 1.0, 1.0, 0.0, \min) + resModOffset;
      //resMod = InFeedback.ar(resModIn).range(resModLo, resModHi);
      filter = BPF.ar(input, centerFreq + centerFreqMod, res + resMod);
      sig = filter * amp;
      sig = filter.distort;
      sig = Out.ar(out, sig);
    }).add;

    /*

    SynthDef(\mod_rlpf, {
      |
      in, out, amp = 1, cutoff = 1000, res = 1,
      cutoffModIn, cutoffModLo, cutoffModHi,
      resModIn, resModLo, resModHi
      |
      var input, cutoffMod, resMod, filter, sig;
      input = InFeedback.ar(in);
      cutoffMod = InFeedback.ar(cutoffModIn).range(cutoffModLo, cutoffModHi);
      resMod = InFeedback.ar(resModIn).range(resModLo, resModHi);
      filter = RLPF.ar(input, cutoff + cutoffMod, res + resMod);
      sig = filter * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

    SynthDef(\mod_rhpf, {
      |
      in, out, amp = 1, cutoff = 1000, res = 1,
      cutoffModIn, cutoffModLo, cutoffModHi,
      resModIn, resModLo, resModHi
      |
      var input, cutoffMod, resMod, filter, sig;
      input = InFeedback.ar(in);
      cutoffMod = InFeedback.ar(cutoffModIn).range(cutoffModLo, cutoffModHi);
      resMod = InFeedback.ar(resModIn).range(resModLo, resModHi);
      filter = RHPF.ar(input, cutoff + cutoffMod, res + resMod);
      sig = filter * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

    */

  }

  prMakeBusses {
    lowPassInBus = Bus.audio;
    highPassInBus = Bus.audio;
    bandPassInBus = Bus.audio;

    lowPassOutBus = Bus.audio;
    highPassOutBus = Bus.audio;
    bandPassOutBus = Bus.audio;

    lowPassCutoffModBus = Bus.audio;
    highPassCutoffModBus = Bus.audio;
    bandPassCutoffModBus = Bus.audio;

    lowPassResModBus = Bus.audio;
    highPassResModBus = Bus.audio;
    bandPassResModBus = Bus.audio;

    nilBus = Bus.audio;
  }

  prFreeBusses {
    lowPassInBus.free;
    highPassInBus.free;
    bandPassInBus.free;

    lowPassOutBus.free;
    highPassOutBus.free;
    bandPassOutBus.free;

    lowPassCutoffModBus.free;
    highPassCutoffModBus.free;
    bandPassCutoffModBus.free;

    lowPassResModBus.free;
    highPassResModBus.free;
    bandPassResModBus.free;

    nilBus.free;
  }

  prMakeSynths { | cutoff = 1000, res = 0, amp = 1 |

    masterCutoff = cutoff;
    lowPassCutoff = masterCutoff;
    highPassCutoff = masterCutoff;
    bandPassCenterFreq = masterCutoff;

    masterRes = res;
    lowPassRes = masterRes;
    highPassRes = masterRes;
    bandPassRes = masterRes;

    masterAmp = amp;
    lowPassAmp = masterAmp;
    highPassAmp = masterAmp;
    bandPassAmp = masterAmp;

    lowPass = Synth(\mod_lowPass, [\in, lowPassInBus, \out, lowPassOutBus, \amp, lowPassAmp, \cutoff, lowPassCutoff,
      \res, lowPassRes]);
    highPass = Synth(\mod_highPass, [\in, highPassInBus, \out, highPassOutBus, \amp, highPassAmp, \cutoff, highPassCutoff,
      \res, highPassRes]);
    bandPass = Synth(\mod_bandPass, [\in, bandPassInBus, \out, bandPassOutBus, \amp, bandPassAmp, \centerFreq, bandPassCenterFreq,
      \res, bandPassRes]);
  }

  prFreeSynths {
    lowPass.free;
    highPass.free;
    bandPass.free;
  }

  setCutoff { | cutoff |
    masterCutoff = cutoff;
    lowPassCutoff = masterCutoff;
    highPassCutoff = masterCutoff;
    bandPassCenterFreq = masterCutoff;
    lowPass.set(\cutoff, lowPassCutoff);
    highPass.set(\cutoff, highPassCutoff);
    bandPass.set(\cutoff, bandPassCenterFreq);
  }

  resetCutoff {
    lowPassCutoff = masterCutoff;
    highPassCutoff = masterCutoff;
    bandPassCenterFreq = masterCutoff;
    lowPass.set(\cutoff, lowPassCutoff);
    highPass.set(\cutoff, highPassCutoff);
    bandPass.set(\cutoff, bandPassCenterFreq);
    ^masterCutoff;
  }

  setLowPassCutoff { | cutoff |
    lowPassCutoff = cutoff;
    lowPass.set(\cutoff, lowPassCutoff);
  }

  setHighPassCutoff { | cutoff |
    highPassCutoff = cutoff;
    highPass.set(\cutoff, highPassCutoff);
  }

  setBandPassCenterFreq { | centerFreq |
    bandPassCenterFreq = centerFreq;
    bandPass.set(\centerFreq, bandPassCenterFreq);
  }


  setRes { | res |
    masterRes = res;
    lowPassRes = masterRes;
    highPassRes = masterRes;
    bandPassRes = masterRes;
    lowPass.set(\res, lowPassRes);
    highPass.set(\res, highPassRes);
    bandPass.set(\res, bandPassRes);
  }

  resetRes {
    lowPassRes = masterRes;
    highPassRes = masterRes;
    bandPassRes = masterRes;
    lowPass.set(\res, lowPassRes);
    highPass.set(\res, highPassRes);
    bandPass.set(\res, bandPassRes);
  }

  setLowPassRes { | res |
    lowPassRes = res;
    lowPass.set(\res, res);
  }

  setHighPassRes { | res |
    highPassRes = res;
    highPass.set(\res, res);
  }

  setBandPassRes { | res |
    bandPassRes = res;
    bandPass.set(\res, res);
  }

  setAmp { | amp |
    masterAmp = amp;
    lowPassAmp = masterAmp;
    highPassAmp = masterAmp;
    bandPassAmp = masterAmp;
    lowPass.set(\amp, lowPassAmp);
    highPass.set(\amp, highPassAmp);
    bandPass.set(\amp, bandPassAmp);

  }

  resetAmp {
    lowPassAmp = masterAmp;
    highPassAmp = masterAmp;
    bandPassAmp = masterAmp;
    lowPass.set(\amp, lowPassAmp);
    highPass.set(\amp, highPassAmp);
    bandPass.set(\amp, bandPassAmp);
  }

  setLowPassAmp { | amp |
    lowPassAmp = amp;
    lowPass.set(\amp, lowPassAmp);
  }

  setHighPassAmp { | amp |
    highPassAmp = amp;
    highPass.set(\amp, highPassAmp);
  }

  setBandPassAmp { | amp |
    bandPassAmp = amp;
    bandPass.set(\amp, bandPassAmp);
  }

}