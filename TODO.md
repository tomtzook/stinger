# TODO

_OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER_

Each assignment should be done in its own branch (created from `master`). When finished, open a Pull Request, but don't merge it.

Assignments to perform:

#### Omer (Omer)

##### Part 1

Implement a `CaptureImageCommand`. This command will open the camera connected to the computer, take a picture and store it as a product.

There are several ways to do so, but we want to do it with _OpenCV_ so you'll learn how to use it.
_OpenCV_ is used for computer vision and can allow you to access cameras.

I've added the opencv library to the project so you can just use it at will. Search about using _OpenCV_ cameras in Java and try to learn it.
Once you've captured the image, you will need to store it in the storage. Convert it to binary data (`byte[]`) and use `BinaryProduct`.


#### Omer (Maayan)

##### Part 1

Implement a `DirListCommand`. This command will list the contents of a directory requested by the user (via parameters). 

You must list the files and directories within it. Use either _java.io_ or _java.nio_ (read online). Store the information in the storage. You can use a `BinaryProduct` and give it a `String` with the information.

The algorithm should go into sub-directories and list their contents as well. You can stop after going 3-levels deep.


#### Omer (Ofek)

##### Part 1

Implement a `RunningProgramsCommand`. This command will list all the running processes on the computer. Each process has a _command_ and _arguments_; return information about those.
Store the information in the storage. You can use a `BinaryProduct` and give it a `String` with the information.

[Hint](https://docs.oracle.com/javase/9/docs/api/java/lang/ProcessHandle.html) 

_OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER OMER_

# Ideas For More
- execute sql
- record video
- record audio
- play audio
