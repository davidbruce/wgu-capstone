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
        character: character,
        actions: actions
    }
}
function getPlayers() {
    return {
            player: generatePlayer(),
            enemy: generatePlayer()
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
        if (ctx.currentPlayer === '0') {
            G.enemy.character.currentHP -= damage(G.player, G.enemy, id);
        } else {
            G.player.character.currentHP -= damage(G.enemy, G.player, id);
        }
    }
  },

  endIf: (G, ctx) => {
    if (ctx.currentPlayer === '0' && G.enemy.character.currentHP < 1) {
      return { winner: ctx.currentPlayer };
    }
    else if (ctx.currentPlayer === '1' && G.player.character.currentHP < 1) {
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


class RPGCombatClient {
  constructor() {
    this.client = BoardgameIO.Client({ game: RPGCombat, debug: true });
    this.client.start();
    this.client.subscribe(state => this.update(state));
  }

  update(state) {
    if (state.ctx.gameover) {
    console.log(state.ctx.gameover.winner);
//      messageEl.textContent =
//        state.ctx.gameover.winner !== undefined
//          ? 'Winner: ' + state.ctx.gameover.winner
//          : 'Draw!';
    } else {
//      messageEl.textContent = '';
    }
  }
}

window.observer = new MutationObserver(function (mutations, mo) {
  var debugPanel = document.getElementsByClassName("debug-panel svelte-1dhkl71")[0];
  if (debugPanel) {
    var ai = document.getElementsByClassName("menu svelte-14p9tpy")[0].lastChild;
    ai.click();
    var checkActive = setInterval(function() {
      if (ai.classList.contains("active")) {
         window.reset = document.getElementById("key-1");
         window.simulate = document.getElementById("key-3");
         document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("span")[0].innerText = 200;
         document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("input")[0].value = 200;
         document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("span")[0].innerText = 15;
         document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("input")[0].value = 15;
         dispatchChangeEvent(document.getElementsByClassName("option svelte-1fu900w")[1].getElementsByTagName("input")[0]);
         dispatchChangeEvent(document.getElementsByClassName("option svelte-1fu900w")[2].getElementsByTagName("input")[0]);
         window.simulate.click();
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
    fetch(url + '/simulate')
        .then(response => response.json())
        .then(data => {
            window.gs = data;
            window.game = new RPGCombatClient();
        });
}