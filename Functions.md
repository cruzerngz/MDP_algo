```java
Object[][] dpGrid = DubinsPathDriver.dubinsPathGrid(double sx, double sy, double syaw, 
                                                    double ex, double ey, double eyaw, double turning_radius);
```
Example output `[[1,1,90], [5,7,270]]` 

<br>
<br>
<br>

```java
Object[] dpInst = DubinsPathDriver.dubinsPathInst(double sx, double sy, double syaw,
                                                  double ex, double ey, double eyaw, double turning_radius);
            
```
Example output `[L, 90, S, 4, R, 90]`

<br>
<br>
<br>

```java
private static String stmConvert(String segment, int amount)
            
```
Example output `\ftrf53;`

<br>
<br>
<br>

## Connectivity

| Algo Receive         |     | Algo Sends                    | Remarks                                                             |
| -------------------- | --- | ----------------------------- | ------------------------------------------------------------------- |
| AND:1 1 90,5 5 270 1 | ->  | AND:Robot started             | Resend to restart everything                                        |
| AND:START            | ->  | STM:\ftrf67;                  | this is the first instruction                                       |
| STM:&                | ->  | STM:\fmf67;                   | this is the next instruction                                        |
| IMG:A                | <-  | IMG:CAP<br>AND:ROBOT,10,10,90 | algo tell camera to take picture, sends current position to android |
| IMG:A                | ->  | AND:TARGET,1,A<br>STM:\fmf8   | send image id to android, tells robot to move next                  |
|                      |     | IMG:DONE                      | path complete                                                       |
