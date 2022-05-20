# artemis-stinger-project

## Stinger

A simple _offline malware_ written it _Java_. It allows performing operations on a remote computer, potentially without the knowledge of the computer users. It communicates with a server periodically, receiving instruction and sending results.

_Stinger_ is based on __Commands__ which are operations that it can perform when instructed to. They produce __Products__ which are stored in the __Storage__. When communicating with the server, _Stinger_ will receive new __Commands__ and send __Products__ in the __Storage__.

### Usage

To run _Stinger_, one can simply run the program using their preferred IDE (_main_ is in `stinger.Main`). Alternatively, one can run the _gradle_ task `run`. The console output will show logging information from the program.
To run the server, run the `run.bat` file. This will open a cmd window which displays output from the server.

_Stinger_ should start communicating with the server periodically.

To send new commands to _Stinger_, put _command files_ in the `stinger-server/commands_out` folder. They will be consumed (deleted) and sent to _Stinger_ when communicating. You can find example _command files_ in `stinger-server/commands` folder.
Products received will be placed in `stinger-server/storage` folder.

#### Trying a Command

Let's try to use the `GetFile` command. This command will copy a file from the computer and send it as a product.

Create a copy of `stinger-server/commands/getfile.command.json`. This is an example file for a `GetFile` command. Open the copy and change the `path` value to equal to the path to the file you want to copy.
Place the created file in `stinger-server/commands_out`.

You will see an output from the server looking somehow like this:
```
[1653057256040] [DEBUG]: Collected new commands: [CommandDefinition{mType=GenericCommandType{mName='GET_FILE', mIntValue=2}, mParameters=Parameters{mParameters={path=/home/tomtzook/projects/frc/artemis/artemis-project/stinger-server/banana}}}]
[1653057259549] [DEBUG]: Doing transaction
[1653057259550] [DEBUG]: Sending command CommandDefinition{mType=GenericCommandType{mName='GET_FILE', mIntValue=2}, mParameters=Parameters{mParameters={path=/home/tomtzook/projects/frc/artemis/artemis-project/stinger-server/banana}}}
```

_Stinger_ will receive this command when communicating with the server and execute it. It should output something like this:
```
[1653059179795] [DEBUG]: Adding commands [Executable{mCommand=stinger.commands.impl.GetFileCommand, mParameters=Parameters{mParameters={path=/home/tomtzook/projects/frc/artemis/artemis-project/stinger-server/banana}}}]
[1653059179795] [DEBUG]: Executing command Executable{mCommand=stinger.commands.impl.GetFileCommand, mParameters=Parameters{mParameters={path=/home/tomtzook/projects/frc/artemis/artemis-project/stinger-server/banana}}}
```

The file will be stored in the storage and send on the next communication. Wait until the server outputs that it received the product:
```
[1653059209796] [DEBUG]: Doing transaction
[1653059209798] [DEBUG]: Received product 5a16733b-033a-47f1-bfec-e65b40d736ce (GenericProductType{mName='FILE', mIntValue=1})
```

Now use the id of the product (`5a16733b-033a-47f1-bfec-e65b40d736ce`) to find the product in the storage folder.

Each command will do something different and product a different result.

### Making Changes

#### Adding New Commands

To add new commands, one needs to implement a new `Command` class and configure both _Stinger_ and the server to recognize it.

Start by creating a new class in `stinger.commands.impl` package. Make it implement `Command`:
```java
class MyCommand implements Command {

    @Override
    public void execute(StingerEnvironment environment, Parameters parameters) throws CommandException {
    
    }
}
```

When executed, the `execute` method is called with several arguments:
- `environment`: provides access to different parts of _Stinger_ for usage.
- `parameters`: contains the parameters send by the user. A parameter is identified by a `String` key and has a value.

Each command perform different operations, so they can look quite different. But generally it involves reading the parameters, performing something and storing the result in the storage.

Commands can fail or succeed. When failing commands will throw `CommandException`, so remember to throw it when unable to perform the wanted operation.

After implementing the command, you must now configure it.
Go to `stinger.commands.StandardCommandType` file. You will see definitions for commands; add a new one.
```java
    MY_COMMAND(id) {
        @Override
        public Command createCommand() {
            return new MyCommand();
        }
    }
```

`id` is a constant number you must choose which identifies your command. Make sure no other command has that id.

Next, go to `stinger-server/command.types.json` and add a definition for you command for the server:
```json
  {
    "mName": "MY_COMMAND",
    "mIntValue": id
  }
```

Remember to set `mIntValue` to your chosen id.

Create an example for the _command file_ in the `stinger-server/commands` folder and name it `mycommand.command.json`:
```json
[
	{
		"mType": id,
		"mParams": {
			"param1": "value"
		}
	}
]
```

Set `mType` to the command id and fill `mParams` with the parameters your command expects (or leave it empty if it doesn't).


##### Using The Storage

The storage is represented by the `Storage` class, which can be retreived from `environment`. It can store `Product` objects. For now, there are two such types: `FileProduct` and `BinaryProduct`.

For example, to store some `String`:
```java
try {
    Storage storage = environment.getStorage();
    storage.store(new BinaryProduct("hello world".getBytes()));
} catch (StorageException e) {
    throw new CommandException(e);
}
```
Note that storage may fail and will throw a `StorageException`. We will just propogate it to fail the command in this case.


