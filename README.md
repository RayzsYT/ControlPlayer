# ControlPlayer

## About
ControlPlayer is a small troll plugin for Minecraft servers.
It allows its user to take full control of its victim. 
Their movement, chat, and even their inventory included!

## Developer API
The API of ControlPlayer allows you to integrate your own actions into the system, overwriting it's origin code entirely.
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
