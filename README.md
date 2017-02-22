1. I introduced an order id to distinguish between different orders when cancelling orders.
2. I separated Order and OrderSummary as the id in Order doesn't make sense when summarising orders,
 the user of the summary doesn't care about the Id.
3. OrderRegistry has 2 maps that represent orders. The reason I did this was for efficiency:
   in a real relational database, the order table  would be indexed. Here that is not possible so I made 2 maps.