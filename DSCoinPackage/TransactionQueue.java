package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
      if(this.numTransactions==0)
      {
          this.firstTransaction = transaction;
          this.lastTransaction = transaction;
      }
      else
      {
          this.lastTransaction.qBehind = transaction;
          this.lastTransaction = transaction;
      }
      this.numTransactions++;
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
      Transaction ans = new Transaction();
      if(this.numTransactions==0)
      {
          throw new EmptyQueueException();
      }
      else
      {
          ans = this.firstTransaction;
          this.firstTransaction = this.firstTransaction.qBehind;
          if(this.numTransactions==1)
          {
              this.lastTransaction = null;
          }
          this.numTransactions--;
      }
      return ans;
  }

  public int size() {
    return this.numTransactions;
  }
}
