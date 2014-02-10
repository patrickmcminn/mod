/*
Friday, February 7th 2014
Mod.sc
prm
*/

Mod {

  var server;
  var group;
  var <patchDict;
  var outDict, oscDict, filterDict, envDict, effectDict, miscDict;

  *new {
    ^super.new.prInit;
  }

  prInit {
    server = Server.default;
    server.waitForBoot {
      this.prMakeDicts;
    };
  }

  prMakeDicts {
    patchDict = IdentityDictionary.new;
    outDict = IdentityDictionary.new;
    oscDict = IdentityDictionary.new;
    filterDict = IdentityDictionary.new;
    envDict = IdentityDictionary.new;
    effectDict = IdentityDictionary.new;
    miscDict = IdentityDictionary.new;
  }

  //////// public functions:

  addOscModule { | name = 'osc', type = 'sine', freq = 220, amp = 0.2 |
    switch(type,
      { 'sine' }, { oscDict[name] = Mod_SinOsc.new(freq, amp); },
      { 'tri' }, { oscDict[name] = Mod_TriOsc.new(freq, amp); },
      { 'square' }, { oscDict[name] = Mod_SquareOsc.new(freq, amp); },
      { 'saw' }, { oscDict[name] = Mod_SawOsc.new(freq, amp); },
      { 'noise' }, { oscDict[name] = Mod_NoiseOsc.new(amp); }
    );
  }

  addSineOscModule { | name = 'sine', freq = 220, amp = 0.2 |
    this.addOscModule(name, 'sine', freq, amp);
  }

  addTriOscModule { | name = 'tri', freq = 220, amp = 0.2 |
    this.addOscModule(name, 'tri', freq, amp);
  }

  addSquareOscModule { | name = 'square', freq = 220, amp = 0.2 |
    this.addOscModule(name, 'square', freq, amp);
  }

  addSawOscModule { | name = 'saw', freq = 220, amp = 0.2 |
    this.addOscModule(name, 'saw', freq, amp);
  }

  addNoiseOscModule { | name = 'noise', amp = 0.2 |
    this.addOscModule(name, 'noise', 220, amp);
  }

  addOutModule { | name, outBus = 0, amp = 1 |
    outDict[name] = Mod_Out.new(outBus, amp);
  }

  module { | name = 'name', type = 'osc' |
    switch(type,
      { 'osc' }, { ^oscDict[name] },
      { 'out' }, { ^outDict[name] },
      //{ 'filter' }, { ^filterDict[name] },
      //{ 'env' }, { ^envDict[name] },
      //{ 'effect' }, { ^effectDict[name] },
      //{ 'misc' }, { ^miscDict[name] }
    );
  }

  oscModule { | name | ^this.module(name, 'osc'); }

  outModule { | name | ^this.module(name, 'out'); }

  //filterModule { | name | ^this.module(name, 'filter'); }

  //envModule { | name | ^this.module(name, 'env'); }

  //effectModule { | name | ^this.module(name, 'effect'); }

  //miscModule { | name | ^this.module(name, 'misc'); }


  /*
  connect { | inputMod, outputMod |
    var connectInDict, connectOutDict;
    var inputStringArray, inputDict, inputModule, inputParam;
    var outputStringArray, outputDict, outputModule, outputParam;
    var keyString, input, output;

    inputStringArray = inputMod.asString.split($.);
    outputStringArray = outputMod.asString.split($.);

    inputDict = inputStringArray[0];
    inputModule = inputStringArray[1];
    inputParam = `inputStringArray[2].asSymbol;

    outputDict = outputStringArray[0];
    outputModule = outputStringArray[1];
    outputParam = outputStringArray[2];

    switch(inputDict.asSymbol,
      { 'out' }, { connectInDict = outDict[inputModule.asSymbol]; },
      { 'osc' }, { connectInDict = oscDict }
    );
    switch(outputDict.asSymbol,
      { 'out' }, { connectOutDict = outDict },
      { 'osc' }, { connectOutDict = oscDict }
    );

    inputParam.postln;
    //(outDict[inputModule.asSymbol].(`inputStringArray[2])).postln;
    //("connectInDict" ++ "[" ++ inputModule.asSymbol ++ "]" ++ "." ++ (inputParam.asSymbol)).interpret.postln;
    connectOutDict[outputModule.asSymbol];

    //input = (connectInDict ++ "['" ++ inputModule ++ "']" ++ "." ++ inputParam);
    //output = (connectOutDict ++ "['" ++ outputModule ++ "']" ++ "." ++ outputParam);
    //keyString = (inputMod ++ outputMod).asSymbol;

    //input.postln;
    //output.postln;

    //patchDict[keyString] = Mod_Patch.new(input.interpret, output.interpret);
  }

  disconnect { | inputMod, outputMod |
    var inputStringArray, inputDict, inputModule, inputParam;
    var outputStringArray, outputDict, outputModule, outputParam;
    var keyString, input, output;

    inputStringArray = inputMod.asString.split($.);
    outputStringArray = outputMod.asString.split($.);

    inputDict = inputStringArray[0];
    inputModule = inputStringArray[1];
    inputParam = inputStringArray[2];
    input = (inputDict ++ "Dict" ++ "['" ++ inputModule ++ "']" ++ "." ++ inputParam);

    outputDict = outputStringArray[0];
    outputModule = outputStringArray[1];
    outputParam = outputStringArray[2];
    output = (outputDict ++ "Dict" ++ "['" ++ outputModule ++ "']" ++ "." ++ outputParam);

    keyString = (inputMod ++ outputMod).asSymbol;

    patchDict[keyString].free;
    patchDict[keyString] = nil;
  }
  */

  connect { | inputModule, outputModule |
    var keyString = (inputModule.asSymbol ++ outputModule.asSymbol).asSymbol;
    patchDict[keyString] = Mod_Patch.new(inputModule, outputModule);
  }

  disconnect { | inputModule, outputModule |
    var keyString = (inputModule.asSymbol ++ outputModule.asSymbol).asSymbol;
    patchDict[keyString].free;
  }

}