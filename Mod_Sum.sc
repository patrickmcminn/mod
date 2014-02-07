/*
Sunday, October 20th 2013
Mod_Sum.sc
prm
*/

Mod_Sum {

  var <>summer, <outBus, <nilBus;

  *new { |in1, in2, in3, in4 |
    ^super.new.prInit(in1, in2, in3, in4);
  }

  prInit { | in1, in2, in3, in4 |
    {
      this.prMakeSynthDefs;
      Server.default.sync;
      this.prMakeBusses;
      Server.default.sync;
      this.prMakeSynths(in1, in2, in3, in4);
    }.fork;
  }

  free {
    this.prFreeSynths;
    this.prFreeBusses;
  }

  prMakeSynthDefs {
    SynthDef(\mod_sum, {
      |
      out, amp = 1,
      in1 = nil, in2 = nil, in3 = nil, in4 = nil,
      in1Gain = 0.25, in2Gain = 0.25, in3Gain = 0.25, in4Gain = 0.25,
      ampModIn, ampModFlag = 0
      |

      var input1, input2, input3, input4, sum, ampMod, sig;
      input1 = InFeedback.ar(in1);
      input2 = InFeedback.ar(in2);
      input3 = InFeedback.ar(in3);
      input4 = InFeedback.ar(in4);
      sum = Mix.ar([input1, input2, input3, input4]);
      ampMod = Select.ar(ampModFlag, [DC.ar(1), In.ar(ampModIn)]);
      sig = sum * ampMod;
      sig = sig * amp;
      sig = sig.distort;
      sig = Out.ar(out, sig);
    }).add;

  }

  prMakeBusses {
    nilBus = Bus.audio;
    outBus = Bus.audio;
  }

  prFreeBusses {
    nilBus.free;
    outBus.free;
  }

  prMakeSynths { | in1, in2, in3, in4 |
    summer = Synth(\mod_sum, [\out, outBus, \in1, nilBus, \in2, nilBus, \in3, nilBus, \in4, nilBus]);

    // doesn't work:
    /*
    in1.notNil {
      summer.set(\in1, in1);
      in1.postln;
    };
    in2.notNil {
      summer.set(\in2, in2);
    };
    in3.notNil {
      summer.set(\in3, in3);
    };
    in4.notNil {
      summer.set(\in4, in4);
    }
    */

  }

  prFreeSynths {
    summer.free;
  }

  setInput { | num = 1, inBus |
    var number = num.asInteger;
    if( number <= 4,
      { summer.set(("in" ++ number).asSymbol, inBus); },
      { ^"only 4 input busses, stupid".postln; }
    );
  }


  // hopefully unnecessary:
  /*
  setInput1 { | inBus |
    summer.set(\in1, inBus);
  }

  setInput2 { | inBus |
    summer.set(\in2, inBus);
  }

  setInput3 { | inBus |
    summer.set(\in3, inBus);
  }

  setInput4 { | inBus |
    summer.set(\in4, inBus);
  }

  setOutput { | outBus |
    summer.set(\out, outBus);
  }
  */



}