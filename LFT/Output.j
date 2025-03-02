.class public Output 
.super java/lang/Object

.method public <init>()V
 aload_0
 invokenonvirtual java/lang/Object/<init>()V
 return
.end method

.method public static print(I)V
 .limit stack 2
 getstatic java/lang/System/out Ljava/io/PrintStream;
 iload_0 
 invokestatic java/lang/Integer/toString(I)Ljava/lang/String;
 invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V
 return
.end method

.method public static read()I
 .limit stack 3
 new java/util/Scanner
 dup
 getstatic java/lang/System/in Ljava/io/InputStream;
 invokespecial java/util/Scanner/<init>(Ljava/io/InputStream;)V
 invokevirtual java/util/Scanner/next()Ljava/lang/String;
 invokestatic java/lang/Integer.parseInt(Ljava/lang/String;)I
 ireturn
.end method

.method public static run()V
 .limit stack 1024
 .limit locals 256
 ldc 2
 dup
 istore 0
 dup
 istore 1
 dup
 istore 2
 pop
 ldc 5
 istore 3
 invokestatic Output/read()I
 istore 4
 invokestatic Output/read()I
 istore 5
 iload 4
 iload 5
 if_icmplt L2
 goto L3
L2:
 ldc 10
 istore 6
 ldc 15
 istore 6
 ldc 0
 istore 7
L6:
 iload 7
 ldc 3
 if_icmplt L7
 goto L8
L7:
 iload 7
 ldc 1
 iadd 
 istore 7
 iload 7
 invokestatic Output/print(I)V
 ldc 0
 istore 6
L11:
 iload 6
 ldc 3
 if_icmplt L12
 goto L13
L12:
 iload 6
 ldc 1
 iadd 
 istore 6
 iload 6
 invokestatic Output/print(I)V
 goto L11
L13:
 goto L6
L8:
 goto L19
L3:
 iload 4
 invokestatic Output/print(I)V
L19:
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

