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

  addModule { | module, name = 'name', type = 'osc' |
    switch(type,
      { 'osc' }, { oscDict[name] = module },
      { 'out' }, { outDict[name] = module },
      { 'filter' }, { filterDict[name] = module },
      { 'env' }, { envDict[name] = module },
      { 'effect' }, { effectDict[name] = module },
      { 'misc' }, { miscDict[name] = module }
    );
  }

  addOscModule { | module, name = 'name' |
    this.addModule(module, name, 'osc');
  }

  addOutModule { | module, name = 'name' |
    this.addModule(module, name, 'out');
  }

  addFilterModule { | module, name = 'name' |
    this.addModule(module, name, 'filter');
  }

  addEnvModule { | module, name = 'name' |
    this.addModule(module, name, 'env');
  }

  addEffectModule { | module, name = 'name' |
    this.addModule(module, name, 'effect');
  }

  addMiscModule { | module, name = 'name' |
    this.addModule(module, name, 'misc');
  }


  module { | name = 'name', type = 'osc' |
    switch(type,
      { 'osc' }, { ^oscDict[name] },
      { 'out' }, { ^outDict[name] },
      { 'filter' }, { ^filterDict[name] },
      { 'env' }, { ^envDict[name] },
      { 'effect' }, { ^effectDict[name] },
      { 'misc' }, { ^miscDict[name] }
    );
  }

  oscModule { | name | ^this.module(name, 'osc'); }

  outModule { | name | ^this.module(name, 'out'); }

  filterModule { | name | ^this.module(name, 'filter'); }

  envModule { | name | ^this.module(name, 'env'); }

  effectModule { | name | ^this.module(name, 'effect'); }

  miscModule { | name | ^this.module(name, 'misc'); }

  connect { | inputModule, outputModule |
    var keyString = (inputModule.asSymbol ++ outputModule.asSymbol).asSymbol;
    patchDict[keyString] = Mod_Patch.new(inputModule, outputModule);
  }

  disconnect { | inputModule, outputModule |
    var keyString = (inputModule.asSymbol ++ outputModule.asSymbol).asSymbol;
    patchDict[keyString].free;
  }

}