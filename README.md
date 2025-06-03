# ControlPlayer

## About
ControlPlayer is a small troll plugin for Minecraft servers.
It allows its users to take full control of their chosen victim.
Their movement, chat, and even their items included!

## Developer API
ControlPlayer's API allows you to add your own actions to the system, overwriting its origin code entirely.
```java
/*
  The types:
    START : When someone starts controlling its victim
    RUNNING : During the time someone is controlling its victim
    START : When someone stops controlling its victim
*/

ControlPlayerEventManager.register(new ControlPlayerEvent() {
    @Override
    public ControlPlayerEventType type() {
        return ControlPlayerEventType.RUNNING; // Types: START, RUNNING, STOP
    }

    @Override
    public void execute(Player controller, Player victim) {
        // Your action
    }
});
```
