/*
Monday, October 21st 2013
Mod_Env.sc
prm
*/

Mod_Env {

  var <>envelope;
  var <envelopeOutBus, nilBus;
  var attack, sustain, release;

  *new {
    ^super.new.prInit

  }

  prInit {

  }

  prMakeSynthDefs {

    SynthDef(\mod_Env, {
      |
      in, out, amp = 1,
      trigType = 0, trigFreq = 1, trigIn,
      trigFreqModIn, trigFreqModIndex = 1, trigFreqModOffset = 0,
      attack = 0.01, sustain = 0.5, release = 0.01
      |

      var input, triggerIn, trigFreqMod, trigger, envelope, sig;

      input = InFeedback.ar(in);
      triggerIn = InFeedback.ar(trigIn);
      trigFreqMod = (InFeedback.ar(trigFreqModIn).linlin(-1.0, 1.0, 0.0, 1.0) * trigFreqModIndex) + trigFreqModOffset;
      trigger = Select.ar(trigType, [Impulse.ar(trigFreq + trigFreqMod), Dust.ar(trigFreq + trigFreqMod), triggerIn]);
      envelope = EnvGen.ar(Env.linen(attack, sustain, release, 1), trigger);
      sig = envelope * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

  }

  makeBusses {
    envelopeOutBus = Bus.audio;
    nilBus = Bus.audio;
  }

  freeBusses {
    envelopeOutBus.free;
    nilBus.free;
  }

  makeSynths { | atk = 0.01, sus = 0.5, rel = 0.01 |
    attack = atk;
    sustain = sus;
    release = rel;
    envelope = Synth(\mod_Env);
  }


  freeSynths {

  }
}