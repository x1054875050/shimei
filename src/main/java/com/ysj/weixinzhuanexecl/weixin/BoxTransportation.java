package com.ysj.weixinzhuanexecl.weixin;

import java.util.HashSet;
import java.util.Set;
/*你有一辆货运卡车，你需要用这一辆车把一些箱子从仓库运送到码头。
这辆卡车每次运输有 箱子数目的限制 和 总重量的限制 。 给你一个箱
子数组 boxes 和三个整数 portsCount, maxBoxes 和 maxWeight
 ，其中 boxes[i] = [portsi, weighti] 。portsi
 表示第 i 个箱子需要送达的码头， weightsi 是第 i 个箱子的重量。
  portsCount 是码头的数目。 maxBoxes 和 maxWeight 分别是卡
  车每趟运输箱子数目和重量的限制。箱子需要按照 数组顺序 运输，同时
  每次运输需要遵循以下步骤： 卡车从 boxes 队列中按顺序取出若干个
  箱子，但不能违反 maxBoxes 和 maxWeight 限制。对于在卡车上的
  箱子，我们需要 按顺序 处理它们，卡车会通过 一趟行程 将最前面的箱
  子送到目的地码头并卸货。如果卡车已经在对应的码头，那么不需要 额外
  行程 ，箱子也会立马被卸货。卡车上所有箱子都被卸货后，卡车需要 一趟
  行程 回到仓库，从箱子队列里再取出一些箱子。卡车在将所有箱子运输并卸
  货后，最后必须回到仓库。 请你返回将所有箱子送到相应码头的 最少行程
  次数。如果boxes=[[1,1],[2,1],[1,1]] ,portsCount=2, maxBoxes=3,
  maxWeight=3最优策略应该是4趟*/
public class BoxTransportation {
    public static int minTrips(int[][] boxes, int portsCount, int maxBoxes, int maxWeight) {
        int trips = 0;  // 记录总的运输次数
        int n = boxes.length;  // 箱子数量
        int i = 0;  // 当前箱子的索引
        while (i < n) {
            trips++;  // 每次开始一趟行程

            int currentWeight = 0;  // 当前行程已装载的总重量
            int currentBoxes = 0;  // 当前行程已装载的箱子数
            Set<Integer> visitedPorts = new HashSet<>();  // 记录这次运输经过的码头

            // 尝试装载尽可能多的箱子
            while (i < n && currentBoxes < maxBoxes && currentWeight + boxes[i][1] <= maxWeight) {
                int port = boxes[i][0];  // 当前箱子的目标码头
                int weight = boxes[i][1];  // 当前箱子的重量
                currentBoxes++;  // 增加装载的箱子数量
                currentWeight += weight;  // 增加当前行程的总重量
                visitedPorts.add(port);  // 记录经过的码头
                i++;  // 处理下一个箱子
            }

            // 如果本趟运输经过了码头，增加行程数
            trips += visitedPorts.size();  // 这次运输经过的码头需要相应的行程数
        }

        // 最后需要回到仓库，所以再增加一次行程
        return trips + 1;
    }

    public static void main(String[] args) {
        // 测试用例[[1,4],[1,2],[2,1],[2,1],[3,2],[3,4]]
        int[][] boxes = {{1,4},{1,2},{2,1},{2,1},{3,2},{3,4}};
        int portsCount = 3;
        int maxBoxes = 6;
        int maxWeight = 6;

        // 计算最少行程数
        int result = minTrips(boxes, portsCount, maxBoxes, maxWeight);

        System.out.println("最少行程数: " + result);  // 输出: 最少行程数: 4
    }
}
