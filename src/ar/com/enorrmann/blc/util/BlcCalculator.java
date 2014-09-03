package ar.com.enorrmann.blc.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BlcCalculator {

	private final static BigDecimal ONE = new BigDecimal(1); 
	public static void main(String[] args) {
		String investedAmount = "0.10000000";
		String rate = "12.00000%";
		int numPayments = 3;
		String payment = getEachPaymentAmount(investedAmount,rate,numPayments);
		System.out.println(payment);

	}
	public static String getEachPaymentAmount(String investedAmountStr, String rateStr, int numPayments){
		BigDecimal investedAmount = new BigDecimal(investedAmountStr);
		BigDecimal rate = new BigDecimal(rateStr.replace("%", ""));
		rate = rate.divide(new BigDecimal(100));
		return getPaymentAmount(investedAmount,rate,numPayments).toString().substring(0,10);
		
	}
	private static BigDecimal getPaymentAmount(BigDecimal investedAmount, BigDecimal rate, int numPayments){
		BigDecimal subPeriodicRate = rate.divide(new BigDecimal(numPayments), 10, RoundingMode.HALF_DOWN);
		BigDecimal dividend = investedAmount.multiply(subPeriodicRate);
		BigDecimal divisor2 = exp(subPeriodicRate.add(ONE),numPayments);
		BigDecimal divisor = ONE.subtract(ONE.divide(divisor2,10, RoundingMode.HALF_DOWN) );
		return dividend.divide(divisor,10, RoundingMode.HALF_DOWN) ;
	}
	
	private static BigDecimal exp(BigDecimal base, int exponent){
		BigDecimal result = base;
		for (int i=1;i<exponent;i++){
			result = result.multiply(base);
		}
		return result;
	}

}
