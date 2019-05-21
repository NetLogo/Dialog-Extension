# Dialog Extension

## What is it?

This is a NetLogo extension for displaying dialog boxes, with NetLogo Web compatibility.  If you do not care about NetLogo Web compatibility, and only care about your model working in the desktop version of NetLogo, you do not need this extension.

This extension can only have at most one dialog box open at any given time.  "Chaining" dialogs is fine, but any attempt to display a new dialog box while an old one is still open will be ignored.

So... okay (chaining):

```
dialog:user-input "What's your name?" [
  name -> dialog:user-message (word "Great!  '" name "' is a good name!") [->]
]
```

Not okay (spamming):

```
dialog:user-input "What's your name?" [->]
dialog:user-message "Who cares?  It's probably not a good name, anyway...." [->]
```

## What problem does this extension solve?

Desktop NetLogo already has built-in primitives with all of the same names as the ones in this extension, but desktop NetLogo's primitives are "synchronous", which is to say that they stop the execution of the model while waiting for the user to interact with the dialog box.  In NetLogo Web, we cannot suspend model execution like that.  Primitives in NetLogo Web must either be synchronous and run very, very quickly, or be "asynchronous" (meaning that they will be initially skipped over, and will then be completed at some unknown point in the future).

The source of this problem is the single-threaded execution model in JavaScript (JavaScript being what NetLogo Web does and must use as its underlying programming language).  JavaScript is hostile towards long-running, synchronous tasks, so, in NetLogo Web, we are forced to display dialog boxes asynchronously.  But if we run `user-one-of` (or any of the other dialog-related primitives) synchronously in desktop NetLogo and asynchronously in NetLogo Web, we cannot guarantee that models will produce the same results.  That's where `dialog` comes in.

With `dialog`, we perform `dialog:user-one-of "What's your favorite fruit?" ["apples" "oranges" "grapes"] [result -> show (word "You are a big fan of " result)]` to display the same dialog box that `user-one-of` would usually display.  Before the dialog box even pops up, though, NetLogo has moved on to the next primitive and executed it.  Eventually, when NetLogo finds a good opportunity, it will display the dialog box.  While the dialog box remains open, NetLogo will continue running any enqueued commands, as if nothing happened.  Then, when the user finally clicks a button in the dialog, we will react to their selection by running the asynchronous function (`[result -> show (word ...)]`), finally bringing the primitive's effect to an end.  This, unlike the synchronous behavior, is functionality that we can guarantee will work the same in both desktop NetLogo and NetLogo Web.

## Primitives

| Prim Name         | Arguments                      | Behavior
| ----------------- | ------------------------------ | --------
| `user-input`      | `message` `callback`           | [See `user-input`](https://ccl.northwestern.edu/netlogo/docs/dictionary.html#user-input)
| `user-message`    | `message` `callback`           | [See `user-message`](https://ccl.northwestern.edu/netlogo/docs/dictionary.html#user-message)
| `user-one-of`     | `message` `options` `callback` | [See `user-one-of`](https://ccl.northwestern.edu/netlogo/docs/dictionary.html#user-one-of)
| `user-yes-or-no?` | `message` `callback`           | [See `user-yes-or-no?`](https://ccl.northwestern.edu/netlogo/docs/dictionary.html#user-yes-or-no)

## Building

Open it in SBT.  If you successfully run `package`, `dialog.jar` is created.

## Terms of Use

[![CC0](http://i.creativecommons.org/p/zero/1.0/88x31.png)](http://creativecommons.org/publicdomain/zero/1.0/)

The NetLogo Dialog extension is in the public domain.  To the extent possible under law, Uri Wilensky has waived all copyright and related or neighboring rights.
