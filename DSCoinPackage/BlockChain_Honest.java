package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
      CRF newCRF = new CRF(64);
      long nonce = 1000000001;
      String prev;
      if(this.lastBlock==null)
      {
          prev = start_string;
      }
      else
      {
          prev = this.lastBlock.dgst;
      }
      String dgst;
      boolean flag = false;
      while(!flag)
      {
          String s = String.valueOf(nonce);
          dgst = newCRF.Fn(prev+"#"+newBlock.trsummary+"#"+s);
          if(dgst.substring(0,4).equals("0000"))
          {
              flag = true;
          }
          else
          {
              nonce++;
          }
      }
      newBlock.nonce = String.valueOf(nonce);
      newBlock.dgst = newCRF.Fn(prev+"#"+newBlock.trsummary+"#"+newBlock.nonce);
      newBlock.previous = this.lastBlock;
      this.lastBlock = newBlock;
  }
}
