window.simulations = 0;
window.maxSimulations = 50;
//window.results = [];

class RPGCombatClient {
  constructor() {
    this.client = BoardgameIO.Client({ game: RPGCombat, debug: true });
    this.client.start();
    this.unsub = this.client.subscribe(state => this.update(state));
  }

  update(state) {
    if (state.deltalog != null) {
        var payload = state.deltalog[0].action.payload;
        var player;
        var action;
        if (payload.playerID === "0") {
//            window.player.actions[payload.args[0]].useCount += 1;
            player = window.player.character.gameSetCharacterId;
            action = window.player.actions[payload.args[0]].gameSetActionId;
            window.enemy.character.currentHP = state.G.enemy.character.currentHP;
        } else {
//            window.enemy.actions[payload.args[0]].useCount += 1;
            player = window.enemy.character.gameSetCharacterId;
            action = window.enemy.actions[payload.args[0]].gameSetActionId;
            window.player.character.currentHP = state.G.player.character.currentHP;
        }
        window.turns.push({
            player: player,
            action: action,
            damage: state.G.damage
        });
    }
    if (state.ctx.gameover) {
        if (window.simulations != window.maxSimulations) {
                var results = {};
                if (state.ctx.gameover.winner === "0") {
                    results.winner = window.player;
                    results.loser = window.enemy;
                }
                else {
                    results.winner = window.enemy;
                    results.loser = window.player;
                }
                results.turns = window.turns;
//                window.results.push(results);

                var url = window.location.href;
                            var request = new Request(url + '/simulate', {
                                method: 'post',
                                headers: {
                                   'Accept': 'application/json, text/plain, */*',
                                   'Content-Type': 'application/json'
                                },
                                body: JSON.stringify(results)
                            });
                          fetch(request)
                                .then(() => {
                                    console.log("Save success");
                                })
                                .catch(() => {
                                    console.log("Failed to save!");
                                });
              window.simulations++;
              console.log(window.simulations);
              window.game.unsub();
              window.game.client.stop();
              window.runGame();
        }

    }
  }
}

function generatePlayer() {
    var character;
    var randomCharacters = window.gs.characters
        .filter(char => char.testCount < window.gs.maxCharacterTestCount);
    if (randomCharacters.length > 0) {
        character = randomCharacters[Math.floor(Math.random() * randomCharacters.length)]
    } else {
        character = window.gs.characters[Math.floor(Math.random() * window.gs.characters.length)]
        window.gs.maxCharacterTestCount++;
    }
    character.testCount++;

    var actions = []
    var randomActions = window.gs.actions
                .filter(action => action.testCount < window.gs.maxActionTestCount)
                .sort(() => Math.random() - 0.5);
    if (randomActions.length > 4) {
            actions = randomActions.slice(0, 4);
    }
    else if (randomActions.length < 4) {
           actions = randomActions.concat(window.gs.actions
                    .filter(action => action.testCount === window.gs.maxActionTestCount)
                    .sort(() => Math.random() - 0.5)
                    .slice(0, 4 - randomActions.length)
           );
           window.gs.maxActionTestCount++
    }
    else {
        actions = randomActions;
    }
    actions.forEach(action => action.testCount++);
    return {
        character: JSON.parse(JSON.stringify(character)),
        actions: JSON.parse(JSON.stringify(actions)) //deep copy actions to allow individual use counts
    }
}
function getPlayers() {
    window.player = generatePlayer();
    window.enemy = generatePlayer();
    //deep copy generated players due to boardgame.io freeezing objects
    return {
            player: JSON.parse(JSON.stringify(window.player)),
            enemy: JSON.parse(JSON.stringify(window.enemy))
    }
}
var RPGCombat = {
  setup: () => getPlayers(),
  turn: {
    moveLimit: 1,
  },

  moves: {
    attack: (G, ctx, id) => {
        if (id < 0 || id > 3) {
            return 'INVALID_MOVE';
        }
        G.damage = 0;
        if (ctx.currentPlayer === '0') {
            G.damage = damage(G.player, G.enemy, id);
            G.enemy.character.currentHP -= G.damage;
        } else {
            G.damage = damage(G.enemy, G.player, id);
            G.player.character.currentHP -= G.damage;
        }
    }
  },

  endIf: (G, ctx) => {
    var finished = false
    if (ctx.currentPlayer === '0' && G.enemy.character.currentHP < 1) {
        finished = true
        return { winner: ctx.currentPlayer };
    }
    else if (ctx.currentPlayer === '1' && G.player.character.currentHP < 1) {
        finished = true
        return { winner: ctx.currentPlayer };
    }
  },

  ai: {
    enumerate: (G, ctx) => {
      let moves = [];
      for (let i = 0; i < 4; i++) {
          moves.push({ move: 'attack', args: [i] });
      }
      return moves;
    }
  }
};

function damage(attacker, defender, action) {
    var move = attacker.actions[action];
    return Math.floor(
            (
                22 * move.damage * (move.category === "Physical" ?
                (attacker.character.phyDef/defender.character.phyDef) :
                (attacker.character.magDef/defender.character.magDef))
                / 50
            ) + 2
    );
}


window.observer = new MutationObserver(function (mutations, mo) {
  var debugPanel = document.getElementsByClassName("debug-panel svelte-1dhkl71")[0];
  if (debugPanel) {
    var ai = document.getElementsByClassName("menu svelte-14p9tpy")[0].lastChild;
    ai.click();
    var checkActive = setInterval(function() {
      if (ai.classList.contains("active")) {
         window.simulate = () => document.getElementById("key-3").click();
         document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("span")[0].innerText = 32;
         document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("input")[0].value = 32;
         document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("span")[0].innerText = 16;
         document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("input")[0].value = 16;
         dispatchChangeEvent(document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("input")[0]);
         dispatchChangeEvent(document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("input")[0]);
         window.simulate();
         clearInterval(checkActive);
      }
    }, 100);
    mo.disconnect();
    return;
  }
});

function dispatchChangeEvent (node) {
    var changeEvent = document.createEvent('HTMLEvents');
    changeEvent.initEvent('change', true, true);
    node.dispatchEvent(changeEvent);
}

window.runGame = function() {
    window.observer.observe(document, {
      childList: true,
      subtree: true
    });
    var url = window.location.href;
    var gamesetId = url.substring(url.lastIndexOf('/') + 1);
    window.turns = [];
    if (window.gs == null) {
        fetch(url + '/simulate')
            .then(response =>
                response.json()
            ).then(data => {
                window.gs = data;
                window.game = new RPGCombatClient();
            });
    }
    else {
        window.game = new RPGCombatClient();
    }
}