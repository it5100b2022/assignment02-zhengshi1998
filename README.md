# IT5100B 2022 - Assignment 2

In this assignment we revisit some of the problems given in 
Assignment 1. However, this time the solution would need to
be Kafka-based.

## Moving Average on Kafka

* Template code in `MovingAverage.scala`; sample test in `TestMovingAverage.scala`

For these exercises we have made the code more compact.
The template also contains the generating code, in
a thread/future that runs concurrently to what is expected to become your solution.
For the Moving Average problem, the thread publishes
on the topic `numbers` some arbitrary numbers, each with
its own key, coming from another small set of numbers.

Given a number `k` (in our case `k=4`), for each number
published, output on the `averages` topic the average 
of the last `k` numbers
with the same key (provided there have been at least `k`
publication for that key -- otherwise no need to publish
anything).

The following is a valid input-output behavior for your solution.


```
      Numbers    |                                  Averages
-----------------------------------------------------------------------------------------
Key     Value    |
-----------------------------------------------------------------------------------------
1       1.0      |
0       2.0      |
1       3.0      |
0       4.0      |
1       5.0      |
0       6.0      |
1       7.0      |     key = 1, numbers to average = [7.0,5.0,3.0,1.0], average = 4.0
0       8.0      |     key = 0, numbers to average = [8.0,6.0,4.0,2.0], average = 5.0
1       9.0      |     key = 1, numbers to average = [9.0,7.0,5.0,3.0], average = 6.0
0       10.0     |     key = 0, numbers to average = [10.0,8.0,6.0,4.0], average = 7.0
1       11.0     |     key = 1, numbers to average = [11.0,9.0,7.0,5.0], average = 8.0
0       12.0     |     key = 0, numbers to average = [12.0,10.0,8.0,6.0], average = 9.0
1       13.0     |     key = 1, numbers to average = [13.0,11.0,9.0,7.0], average = 10.0
0       14.0     |     key = 0, numbers to average = [14.0,12.0,10.0,8.0], average = 11.0
1       15.0     |     key = 1, numbers to average = [15.0,13.0,11.0,9.0], average = 12.0
0       16.0     |     key = 0, numbers to average = [16.0,14.0,12.0,10.0], average = 13.0
1       17.0     |     key = 1, numbers to average = [17.0,15.0,13.0,11.0], average = 14.0
0       18.0     |     key = 0, numbers to average = [18.0,16.0,14.0,12.0], average = 15.0
1       19.0     |     key = 1, numbers to average = [19.0,17.0,15.0,13.0], average = 16.0
0       20.0     |     key = 0, numbers to average = [20.0,18.0,16.0,14.0], average = 17.0
1       21.0     |     key = 1, numbers to average = [21.0,19.0,17.0,15.0], average = 18.0
0       22.0     |     key = 0, numbers to average = [22.0,20.0,18.0,16.0], average = 19.0
1       23.0     |     key = 1, numbers to average = [23.0,21.0,19.0,17.0], average = 20.0
0       24.0     |     key = 0, numbers to average = [24.0,22.0,20.0,18.0], average = 21.0
1       25.0     |     key = 1, numbers to average = [25.0,23.0,21.0,19.0], average = 22.0
0       26.0     |     key = 0, numbers to average = [26.0,24.0,22.0,20.0], average = 23.0
1       27.0     |     key = 1, numbers to average = [27.0,25.0,23.0,21.0], average = 24.0
0       28.0     |     key = 0, numbers to average = [28.0,26.0,24.0,22.0], average = 25.0
1       29.0     |     key = 1, numbers to average = [29.0,27.0,25.0,23.0], average = 26.0
0       30.0     |     key = 0, numbers to average = [30.0,28.0,26.0,24.0], average = 27.0

```

The numbers on the left column are produced by the thread in the template. The numbers in the right column
must be produced by your solution.

The gaps in the second column are to better reflect the correspondence between input and output. These
gaps will not appear on the screen.

The places where you have to insert code are marked as `???`. Please replace those symbols with valid code.
Specifically, complete the body of the `makeTopology` method, and the corresponding serializers/deserializers.
When the code is correct, the test in `TestMovingAverage.scala` should also pass. Use this test as a model
and add more tests to make sure that your solution is correct.

## Maximum Gains on Kafka

In this exercise we assume that the input topic contains asset prices on a trading platform.
For every publication, the key is a *ticker symbol*, and the published value is the price of 1 unit
of the give asset (e.g. shares/equity).

The idea is, of course, to buy low and sell high.

For each published price, compute and publish on the output topic the maximum possible gain attainable for that
moment, assuming that at a prior moment you have bought 1 unit of the asset as the lowest price you observed so far.
You need to publish a result only if the gain is strictly positive (i.e. no zero gains) -- do not publish anything
if a positive gain is not possible.

Use `prices` as the input topic on Kafka, and `gains` as the output topic.

The thread/future included in your solution template will generate some numbers that go up and down, simulating
real stock prices. 

The following would be a correct behavior snippet for your solution:

```
   prices      |                                       gains
----------------------------------------------------------------------------------------------------------   
key     value  |                                    
----------------
AMZN    1010   |
MSFT    1020   |
GOOGL   1030   |
NFLX    1040   |
NVDA    1050   |
AAPL    1060   |
AMZN    1069   |    asset: AMZN, current price: 1069.0, lowest previous value: 1010.0, gain: 59.0
MSFT    1078   |    asset: MSFT, current price: 1078.0, lowest previous value: 1020.0, gain: 58.0
GOOGL   1086   |    asset: GOOGL, current price: 1086.0, lowest previous value: 1030.0, gain: 56.0
NFLX    1093   |    asset: NFLX, current price: 1093.0, lowest previous value: 1040.0, gain: 53.0
NVDA    1099   |    asset: NVDA, current price: 1099.0, lowest previous value: 1050.0, gain: 49.0
AAPL    1105   |    asset: AAPL, current price: 1105.0, lowest previous value: 1060.0, gain: 45.0
AMZN    1110   |    asset: AMZN, current price: 1110.0, lowest previous value: 1010.0, gain: 100.0
MSFT    1113   |    asset: MSFT, current price: 1113.0, lowest previous value: 1020.0, gain: 93.0
GOOGL   1116   |    asset: GOOGL, current price: 1116.0, lowest previous value: 1030.0, gain: 86.0
NFLX    1117   |    asset: NFLX, current price: 1117.0, lowest previous value: 1040.0, gain: 77.0
NVDA    1117   |    asset: NVDA, current price: 1117.0, lowest previous value: 1050.0, gain: 67.0
AAPL    1116   |    asset: AAPL, current price: 1116.0, lowest previous value: 1060.0, gain: 56.0
AMZN    1114   |    asset: AMZN, current price: 1114.0, lowest previous value: 1010.0, gain: 104.0
MSFT    1111   |    asset: MSFT, current price: 1111.0, lowest previous value: 1020.0, gain: 91.0
GOOGL   1106   |    asset: GOOGL, current price: 1106.0, lowest previous value: 1030.0, gain: 76.0
NFLX    1100   |    asset: NFLX, current price: 1100.0, lowest previous value: 1040.0, gain: 60.0
NVDA    1093   |    asset: NVDA, current price: 1093.0, lowest previous value: 1050.0, gain: 43.0
AAPL    1085   |    asset: AAPL, current price: 1085.0, lowest previous value: 1060.0, gain: 25.0
AMZN    1076   |    asset: AMZN, current price: 1076.0, lowest previous value: 1010.0, gain: 66.0
MSFT    1066   |    asset: MSFT, current price: 1066.0, lowest previous value: 1020.0, gain: 46.0
GOOGL   1055   |    asset: GOOGL, current price: 1055.0, lowest previous value: 1030.0, gain: 25.0
NFLX    1043   |    asset: NFLX, current price: 1043.0, lowest previous value: 1040.0, gain: 3.0
NVDA    1031   |
AAPL    1018   |
AMZN    1005   |
MSFT    992    |
GOOGL   978    |
NFLX    964    |
NVDA    951    |
AAPL    938    |
AMZN    925    |
MSFT    912    |
GOOGL   901    |
NFLX    890    |
NVDA    880    |
AAPL    872    |
AMZN    864    |
MSFT    858    |
GOOGL   853    |
NFLX    849    |
NVDA    847    |
AAPL    847    |
AMZN    848    |
MSFT    851    |
GOOGL   855    |    asset: GOOGL, current price: 855.0, lowest previous value: 853.0, gain: 2.0
NFLX    860    |    asset: NFLX, current price: 860.0, lowest previous value: 849.0, gain: 11.0
NVDA    868    |    asset: NVDA, current price: 868.0, lowest previous value: 847.0, gain: 21.0
AAPL    876    |    asset: AAPL, current price: 876.0, lowest previous value: 847.0, gain: 29.0
AMZN    886    |    asset: AMZN, current price: 886.0, lowest previous value: 848.0, gain: 38.0
MSFT    897    |    asset: MSFT, current price: 897.0, lowest previous value: 851.0, gain: 46.0
GOOGL   910    |    asset: GOOGL, current price: 910.0, lowest previous value: 853.0, gain: 57.0
NFLX    923    |    asset: NFLX, current price: 923.0, lowest previous value: 849.0, gain: 74.0
NVDA    938    |    asset: NVDA, current price: 938.0, lowest previous value: 847.0, gain: 91.0
AAPL    953    |    asset: AAPL, current price: 953.0, lowest previous value: 847.0, gain: 106.0
AMZN    969    |    asset: AMZN, current price: 969.0, lowest previous value: 848.0, gain: 121.0
MSFT    986    |    asset: MSFT, current price: 986.0, lowest previous value: 851.0, gain: 135.0
GOOGL   1002   |    asset: GOOGL, current price: 1002.0, lowest previous value: 853.0, gain: 149.0
NFLX    1019   |    asset: NFLX, current price: 1019.0, lowest previous value: 849.0, gain: 170.0
NVDA    1036   |    asset: NVDA, current price: 1036.0, lowest previous value: 847.0, gain: 189.0
AAPL    1053   |    asset: AAPL, current price: 1053.0, lowest previous value: 847.0, gain: 206.0
AMZN    1070   |    asset: AMZN, current price: 1070.0, lowest previous value: 848.0, gain: 222.0
MSFT    1085   |    asset: MSFT, current price: 1085.0, lowest previous value: 851.0, gain: 234.0
GOOGL   1101   |    asset: GOOGL, current price: 1101.0, lowest previous value: 853.0, gain: 248.0
NFLX    1115   |    asset: NFLX, current price: 1115.0, lowest previous value: 849.0, gain: 266.0
NVDA    1128   |    asset: NVDA, current price: 1128.0, lowest previous value: 847.0, gain: 281.0
AAPL    1141   |    asset: AAPL, current price: 1141.0, lowest previous value: 847.0, gain: 294.0
AMZN    1152   |    asset: AMZN, current price: 1152.0, lowest previous value: 848.0, gain: 304.0
MSFT    1161   |    asset: MSFT, current price: 1161.0, lowest previous value: 851.0, gain: 310.0
GOOGL   1169   |    asset: GOOGL, current price: 1169.0, lowest previous value: 853.0, gain: 316.0
NFLX    1175   |    asset: NFLX, current price: 1175.0, lowest previous value: 849.0, gain: 326.0
NVDA    1180   |    asset: NVDA, current price: 1180.0, lowest previous value: 847.0, gain: 333.0
AAPL    1183   |    asset: AAPL, current price: 1183.0, lowest previous value: 847.0, gain: 336.0
AMZN    1184   |    asset: AMZN, current price: 1184.0, lowest previous value: 848.0, gain: 336.0
MSFT    1183   |    asset: MSFT, current price: 1183.0, lowest previous value: 851.0, gain: 332.0
GOOGL   1180   |    asset: GOOGL, current price: 1180.0, lowest previous value: 853.0, gain: 327.0
NFLX    1176   |    asset: NFLX, current price: 1176.0, lowest previous value: 849.0, gain: 327.0
NVDA    1169   |    asset: NVDA, current price: 1169.0, lowest previous value: 847.0, gain: 322.0
AAPL    1161   |    asset: AAPL, current price: 1161.0, lowest previous value: 847.0, gain: 314.0


```

The numbers on the left column are produced by the thread in the template. The numbers in the right column
must be produced by your solution.

The gaps in the second column are to better reflect the correspondence between input and output. These
gaps will not appear on the screen.

The places where you have to insert code are marked as `???`. Please replace those symbols with valid code.
Specifically, complete the body of the `makeTopology` method, and the corresponding serializers/deserializers.
When the code is correct, the test in `TestMaximumGains.scala` should also pass. Use this test as a model
and add more tests to make sure that your solution is correct.

Specifically, complete the body of the `makeTopology` method, and the corresponding serializers/deserializers.

### Happy coding!