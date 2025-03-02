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
 ldc 0
 istore 0
L0:
 iload 0
 ldc 10
 if_icmplt L1
 goto L2
L1:
 ldc 0
 istore 1
L3:
 iload 1
 ldc 10
 if_icmplt L4
 goto L5
L4:
 iload 1
 ldc 1
 iadd 
 istore 1
 iload 1
 invokestatic Output/print(I)V
 goto L3
L5:
 iload 0
 ldc 1
 iadd 
 istore 0
 iload 0
 invokestatic Output/print(I)V
 goto L0
L2:
 invokestatic Output/read()I
 istore 2
 iload 2
 ldc 10
 if_icmpne L13
 goto L14
L13:
 ldc 100
 invokestatic Output/print(I)V
 goto L17
L14:
 ldc 100
 ineg 
 invokestatic Output/print(I)V
L17:
 ldc 5
 dup
 istore 3
 dup
 istore 4
 pop
 ldc 10
 istore 5
 iload 2
 iload 3
 iload 4
 idiv 
 imul 
 iload 5
 imul 
 invokestatic Output/print(I)V
 return
.end method

.method public static main([Ljava/lang/String;)V
 invokestatic Output/run()V
 return
.end method

