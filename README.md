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

