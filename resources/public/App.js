function getPlayers() {
    return {
                     player: {
                       name: "Test",
                       type: "Fire",
                       hp: 100,
                       stats: {
                           maxHP: 100,
                           phyAttack: 100,
                           magAttack: 100,
                           phyDefense: 100,
                           magDefense: 100,
                           speed: 100
                       },
                       moves: [
                          {name: "Fire", damage: 45, category: "Magical", effect: ""},
                          {name: "Fire", damage: 45, category: "Magical", effect: ""},
                          {name: "Fire", damage: 45, category: "Magical", effect: ""},
                          {name: "Fire", damage: 45, category: "Magical", effect: ""}
                       ]
                     },
                     enemy: {
                         name: "Test2",
                         type: "Fire",
                         hp: 100,
                         stats: {
                             maxHP: 100,
                             phyAttack: 100,
                             magAttack: 100,
                             phyDefense: 100,
                             magDefense: 100,
                             speed: 100
                         },
                         moves: [
                            {name: "Fire", damage: 45, category: "Magical", effect: ""},
                            {name: "Fire", damage: 45, category: "Magical", effect: ""},
                            {name: "Fire", damage: 45, category: "Magical", effect: ""},
                            {name: "Fire", damage: 45, category: "Magical", effect: ""}
                         ]
                     }
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
            G.enemy.hp -= damage(G.player, G.enemy, id);
        } else {
            G.player.hp -= damage(G.enemy, G.player, id);
        }
    }
  },

  endIf: (G, ctx) => {
    if (ctx.currentPlayer === '0' && G.enemy.hp < 1) {
      return { winner: ctx.currentPlayer };
    }
    else if (ctx.currentPlayer === '1' && G.player.hp < 1) {
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

function damage(attacker, defender, move) {
    var move = attacker.moves[move];
    return Math.floor(((22 * move.damage * (move.category === "Physical" ? (attacker.stats.phyDefense/defender.stats.phyDefense) : (attacker.stats.magDefense/defender.stats.magDefense))
            / 50) + 2)
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
    window.game = new RPGCombatClient();
}

