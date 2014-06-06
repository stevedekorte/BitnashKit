package org.bitmarkets.bitnash;

import java.math.BigInteger;
import java.util.Arrays;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutPoint;
import com.google.bitcoin.core.TransactionOutput;

public class BNTxOut extends BNObject {
	public static BNTxOut fromOutpoint(TransactionOutPoint outpoint) {
		BNTx bnTx = new BNTx();
		bnTx.setBnParent(BNWallet.shared());
		bnTx.setTransaction(outpoint.getConnectedOutput().getParentTransaction());
		bnTx.willSerialize();
		
		return (BNTxOut) bnTx.getOutputs().get((int)outpoint.getIndex());
	}
	
	public Number getValue() {
		return value;
	}
	
	public void setValue(Number value) {
		this.value = value;
	}
	
	public BNScriptPubKey getScriptPubKey() {
		return scriptPubKey;
	}
	
	public void setScriptPubKey(BNScriptPubKey scriptPubKey) {
		this.scriptPubKey = scriptPubKey;
	}
	
	public int index() {
		return bnTx().getOutputs().indexOf(this);
	}
	
	@SuppressWarnings("unchecked")
	public void lock() {
		metaData.put("isLocked", Boolean.valueOf(true));
	}
	
	@SuppressWarnings("unchecked")
	public void unlock() {
		metaData.put("isLocked", Boolean.valueOf(false));
	}
	
	public String id() {
		return bnTx().id() + "." + Integer.toHexString(index());
	}
	
	BNTx bnTx() {
		return (BNTx) getParent();
	}
	
	Transaction transaction() {
		return bnTx().getTransaction();
	}
	
	TransactionOutput transactionOutput() {
		return transaction().getOutput(index());
	}
	
	Number value;
	BNScriptPubKey scriptPubKey;
	
	public BNTxOut() {
		super();
		bnSlotNames.addAll(Arrays.asList("value", "scriptPubKey"));
	}
	
	void didDeserializeSelf() {
		//delegate this to script
	}
	
	void willSerializeSelf() {
		TransactionOutput transactionOutput = transactionOutput();
		
		if (transactionOutput.getScriptPubKey().isSentToMultiSig()) {
			scriptPubKey = new BNMultisigScriptPubKey();
		} else {
			scriptPubKey = new BNPayToAddressScriptPubKey();
		}
		
		scriptPubKey.setParent(this);
		scriptPubKey.willSerialize();
		
		setScriptPubKey(scriptPubKey);
		
		setValue(BigInteger.valueOf(transactionOutput.getValue().longValue()));
	}
}
