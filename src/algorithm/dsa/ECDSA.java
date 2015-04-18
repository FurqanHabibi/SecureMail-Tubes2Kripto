package algorithm.dsa;

import java.math.BigInteger;
import java.util.Random;

import algorithm.dsa.ECC;
import algorithm.dsa.Point;



public class ECDSA {

	private String signature;
	private String message;
	private Point G; // generator point
	private BigInteger n;
	private BigInteger e; // e=H(m), H is hash function: SHA1
	private BigInteger dA; // private key dA
	private Point QA; // public key QA = dA * G
	private BigInteger r; // r = x1 (mod n), where (x1, y1) = k * G. r !=0
	private BigInteger s; // s = k-1 (e + dAr)(mod n), s != 0
	private BigInteger w;
	private BigInteger u1;
	private BigInteger u2;
	private BigInteger k;

	public ECDSA() {

	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getE() {
		return e;
	}

	public void setE(BigInteger e) {
		this.e = e;
	}

	public BigInteger getW() {
		return w;
	}

	public void setW(BigInteger w) {
		this.w = w;
	}

	public BigInteger getU1() {
		return u1;
	}

	public void setU1(BigInteger u1) {
		this.u1 = u1;
	}

	public BigInteger getU2() {
		return u2;
	}

	public void setU2(BigInteger u2) {
		this.u2 = u2;
	}

	public BigInteger getK() {
		return k;
	}

	public void setK(BigInteger k) {
		this.k = k;
	}

	public void setN(BigInteger n) {
		this.n = n;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Point getG() {
		return G;
	}

	public void setG(Point g) {
		G = g;
	}

	public BigInteger getdA() {
		return dA;
	}

	public void setdA(BigInteger dA) {
		this.dA = dA;
	}

	public Point getQA() {
		return QA;
	}

	public void setQA(Point qA) {
		QA = qA;
	}

	public BigInteger getR() {
		return r;
	}

	public void setR(BigInteger r) {
		this.r = r;
	}

	public BigInteger getS() {
		return s;
	}

	public void setS(BigInteger s) {
		this.s = s;
	}

	public void cal_e() {
		SHA1 sha1 = new SHA1(message);
		String hashed = sha1.hash();
		e = new BigInteger(hashed, 16);
	}

	public static BigInteger generatePrivateKeyECDSA(BigInteger n) {
		return new BigInteger(192, new Random())
				.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
	}

	public static Point generatePublicKeyECDSA(BigInteger dA, Point G) {
		ECC.setParam(new BigInteger("1"),
				new BigInteger("6"), new BigInteger("11"), new Point(new BigInteger(
						"2"), new BigInteger("4")));
		return ECC.times(dA, G);
	}

	public void signatureGeneration(BigInteger privateK, String _message,
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger _n) {
		
		G = _G;
		ECC.setParam(a, b, p, G);
		dA = privateK;
		message = _message;
	
		cal_e();
		n = _n;
		do {
			k = new BigInteger(192, new Random()).mod(
					n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
			Point kP = ECC.times(k, G);
			r = kP.x.mod(n);
			s = BigInteger.ONE.divide(k).multiply(e.add(dA.multiply(r))).mod(n);
		} while (r.compareTo(BigInteger.ZERO) == 0
				|| s.compareTo(BigInteger.ZERO) == 0);

	}

	public boolean verifySignature(Point publicK,  String _message,
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger _n, BigInteger r, BigInteger s) {
		boolean val = false;

		ECC.setParam(a, b, p, G);
		QA = publicK;
		message=_message;

		if (r.compareTo(n) != -1
				|| r.compareTo(BigInteger.ZERO) != 1) {
			return val;
		}

		if (s.compareTo(n) != -1
				|| s.compareTo(BigInteger.ZERO) != 1) {
			return val;
		}

		cal_e();
		w = s.modInverse(n);
		u1 = e.multiply(w).mod(n);
		u2 = r.multiply(w).mod(n);
		Point P = ECC.add(ECC.times(u1, G), ECC.times(u2, QA));
		if (P.x.equals(r.mod(n)))
			val = true;

		return val;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BigInteger pri = ECDSA.generatePrivateKeyECDSA(new BigInteger("13"));
		Point pub = ECDSA.generatePublicKeyECDSA(pri, new Point(new BigInteger(
				"2"), new BigInteger("4")));
		ECDSA ecdsa = new ECDSA();
		
		ecdsa.signatureGeneration(pri, "Alifa Nurani Putri syslalallalalala wkwk!?=0", new BigInteger("1"),
				new BigInteger("6"), new BigInteger("11"), new Point(new BigInteger(
						"2"), new BigInteger("4")), new BigInteger("13"));
		
		boolean valid = ecdsa.verifySignature(pub, "Alifa Nurani Putri syslalallalalala wkwk!?=0", new BigInteger("1"),
				new BigInteger("6"), new BigInteger("11"), new Point(new BigInteger(
						"2"), new BigInteger("4")), new BigInteger("13"), ecdsa.getR(), ecdsa.getS());
		
		if (valid)
			System.out.println("benar");
		else 
			System.out.println("salah");

	}

}
