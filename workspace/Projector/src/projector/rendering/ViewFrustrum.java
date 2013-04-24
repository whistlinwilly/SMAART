package projector.rendering;

public class ViewFrustrum {
	float nA, nB, nC, nD;
	float fA, fB, fC, fD;
	float lA, lB, lC, lD;
	float rA, rB, rC, rD;
	float tA, tB, tC, tD;
	float bA, bB, bC, bD;
	float[] point = new float[3];

	
	//NOTE: CHANGED THE FOV IN MATH.TAN TO BE 35!!!!!!
	
	
	public void initializeFrustrum(float[] eye, float[] center, float[] up, float fov, float aspectRatio, float nearPlane, float farPlane){
		float nearHeight = (float) (2.0f * Math.tan((35.0f / 2) / 360.0 * Math.PI) * nearPlane);
		float farHeight = (float) (2.0f * Math.tan((35.0f / 2) / 360.0 * Math.PI) * farPlane);
		float nearWidth = nearHeight * aspectRatio;
		float farWidth = farHeight * aspectRatio;
		float[] fc = new float[3];
		float[] ftl = new float[3];
		float[] ftr = new float[3];
		float[]	fbl = new float[3];
		float[] fbr = new float[3];
		float[] nc = new float[3];
		float[] ntl = new float[3];
		float[] ntr = new float[3];
		float[] nbl = new float[3];
		float[] nbr = new float[3];
		float[] d = new float[3];
		float[] right = new float[3];
		float[] u = new float[3];
		float[] v = new float[3];
		float[] n = new float[3];
		float bigD;
		float normalize;
		
		point[0] = eye[0];
		point[1] = eye[1];
		point[2] = eye[2];
		
		//lookat vector
		d[0] = center[0] - eye[0];
		d[1] = center[1] - eye[1];
		d[2] = center[2] - eye[2];
		
		normalize = (float) Math.sqrt(Math.pow(d[0],2) + Math.pow(d[1], 2) + Math.pow(d[2], 2));
		
		d[0] /= normalize;
		d[1] /= normalize;
		d[2] /= normalize;
		
		//AyBz - AzBy
		right[0] = d[1] * up[2] - d[2] * up[1];
		
		//AzBx - AxBz
		right[1] = d[2] * up[0] - d[0] * up[2];
		
		//AxBy - AyBx
		right[2] = d[0] * up[1] - d[1] * up[0];
		
		//far plane center
		fc[0] = eye[0] + d[0] * farPlane;
		fc[1] = eye[1] + d[1] * farPlane;
		fc[2] = eye[2] + d[2] * farPlane;
		
		//far top left
		ftl[0] = fc[0] + (up[0] *  farHeight / 2) - (right[0] * farWidth / 2);
		ftl[1] = fc[1] + (up[1] *  farHeight / 2) - (right[1] * farWidth / 2);
		ftl[2] = fc[2] + (up[2] *  farHeight / 2) - (right[2] * farWidth / 2);
		
		//far top right
		ftr[0] = fc[0] + (up[0] *  farHeight / 2) + (right[0] * farWidth / 2);
		ftr[1] = fc[1] + (up[1] *  farHeight / 2) + (right[1] * farWidth / 2);
		ftr[2] = fc[2] + (up[2] *  farHeight / 2) + (right[2] * farWidth / 2);
		
		//far bottom left
		fbl[0] = fc[0] - (up[0] * farHeight / 2) - (right[0] * farWidth / 2);
		fbl[1] = fc[1] - (up[1] * farHeight / 2) - (right[1] * farWidth / 2);
		fbl[2] = fc[2] - (up[2] * farHeight / 2) - (right[2] * farWidth / 2);
		
		//far bottom right
		fbr[0] = fc[0] - (up[0] * farHeight / 2) + (right[0] * farWidth / 2);
		fbr[1] = fc[1] - (up[1] * farHeight / 2) + (right[1] * farWidth / 2);
		fbr[2] = fc[2] - (up[2] * farHeight / 2) + (right[2] * farWidth / 2);
		
		//near plane center
		nc[0] = eye[0] + d[0] * nearPlane;
		nc[1] = eye[1] + d[1] * nearPlane;
		nc[2] = eye[2] + d[2] * nearPlane;
		
		//near top left
		ntl[0] = nc[0] + (up[0] * nearHeight / 2) - (right[0] * nearWidth / 2);
		ntl[1] = nc[1] + (up[1] * nearHeight / 2) - (right[1] * nearWidth / 2);
		ntl[2] = nc[2] + (up[2] * nearHeight / 2) - (right[2] * nearWidth / 2);
		
		//near top right
		ntr[0] = nc[0] + (up[0] * nearHeight / 2) + (right[0] * nearWidth / 2);
		ntr[1] = nc[1] + (up[1] * nearHeight / 2) + (right[1] * nearWidth / 2);
		ntr[2] = nc[2] + (up[2] * nearHeight / 2) + (right[2] * nearWidth / 2);
		
		//near bottom left
		nbl[0] = nc[0] - (up[0] * nearHeight / 2) - (right[0] * nearWidth / 2);
		nbl[1] = nc[1] - (up[1] * nearHeight / 2) - (right[1] * nearWidth / 2);
		nbl[2] = nc[2] - (up[2] * nearHeight / 2) - (right[2] * nearWidth / 2);
		
		//near bottom right
		nbr[0] = nc[0] - (up[0] * nearHeight / 2) + (right[0] * nearWidth / 2);
		nbr[1] = nc[1] - (up[1] * nearHeight / 2) + (right[1] * nearWidth / 2);
		nbr[2] = nc[2] - (up[2] * nearHeight / 2) + (right[2] * nearWidth / 2);
		
		//near plane
		// v = p1 - p0
		v[0] = ntl[0] - nbl[0];
		v[1] = ntl[1] - nbl[1];
		v[2] = ntl[2] - nbl[2];
		
		// u = p2 - p0
		u[0] = nbr[0] - nbl[0];
		u[1] = nbr[1] - nbl[1];
		u[2] = nbr[2] - nbl[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * ntr[0] + n[1] * ntr[1] + n[2] * ntr[2]);
		
		nA = n[0];
		nB = n[1];
		nC = n[2];
		nD = bigD;
		
		//far plane
		// v = p1 - p0
		v[0] = ftr[0] - fbr[0];
		v[1] = ftr[1] - fbr[1];
		v[2] = ftr[2] - fbr[2];
		
		// u = p2 - p0
		u[0] = fbl[0] - fbr[0];
		u[1] = fbl[1] - fbr[1];
		u[2] = fbl[2] - fbr[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * ftl[0] + n[1] * ftl[1] + n[2] * ftl[2]);
		
		fA = n[0];
		fB = n[1];
		fC = n[2];
		fD = bigD;
		
		//left plane
		// v = p1 - p0
		v[0] = ftl[0] - fbl[0];
		v[1] = ftl[1] - fbl[1];
		v[2] = ftl[2] - fbl[2];
		
		// u = p2 - p0
		u[0] = nbl[0] - fbl[0];
		u[1] = nbl[1] - fbl[1];
		u[2] = nbl[2] - fbl[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * ntl[0] + n[1] * ntl[1] + n[2] * ntl[2]);
		
		lA = n[0];
		lB = n[1];
		lC = n[2];
		lD = bigD;
		
		//right plane
		// v = p1 - p0
		v[0] = ntr[0] - nbr[0];
		v[1] = ntr[1] - nbr[1];
		v[2] = ntr[2] - nbr[2];
		
		// u = p2 - p0
		u[0] = fbr[0] - nbr[0];
		u[1] = fbr[1] - nbr[1];
		u[2] = fbr[2] - nbr[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * ftr[0] + n[1] * ftr[1] + n[2] * ftr[2]);
		
		rA = n[0];
		rB = n[1];
		rC = n[2];
		rD = bigD;
		
		//top plane
		// v = p1 - p0
		v[0] = ntr[0] - ftr[0];
		v[1] = ntr[1] - ftr[1];
		v[2] = ntr[2] - ftr[2];
		
		// u = p2 - p0
		u[0] = ftl[0] - ftr[0];
		u[1] = ftl[1] - ftr[1];
		u[2] = ftl[2] - ftr[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * ntl[0] + n[1] * ntl[1] + n[2] * ntl[2]);
		
		tA = n[0];
		tB = n[1];
		tC = n[2];
		tD = bigD;
		
		//bottom plane
		// v = p1 - p0
		v[0] = nbl[0] - fbl[0];
		v[1] = nbl[1] - fbl[1];
		v[2] = nbl[2] - fbl[2];
		
		// u = p2 - p0
		u[0] = fbr[0] - fbl[0];
		u[1] = fbr[1] - fbl[1];
		u[2] = fbr[2] - fbl[2];
		
		// n = v X u
		
		//VyUz - VzUy
		n[0] = v[1] * u[2] - v[2] * u[1];
		
		//VzUx - VxUz
		n[1] = v[2] * u[0] - v[0] * u[2];
		
		//VxUy - VyUx
		n[2] = v[0] * u[1] - v[1] * u[0];
		
		normalize = (float) Math.sqrt(Math.pow(n[0],2) + Math.pow(n[1], 2) + Math.pow(n[2], 2));
		
		n[0] /= normalize;
		n[1] /= normalize;
		n[2] /= normalize;
		
		bigD = -1.0f * (n[0] * nbr[0] + n[1] * nbr[1] + n[2] * nbr[2]);
		
		bA = n[0];
		bB = n[1];
		bC = n[2];
		bD = bigD;
		
	}
	
	public boolean pointInFrustrum(float x, float y, float z){
		
		boolean fuckthis = false;
		
		//Top Plane
		fuckthis = (tA * x + tB * y + tC * z + tD < 0);
		if(fuckthis)
			return false;
		
		//Bottom Plane
		fuckthis = (bA * x + bB * y + bC * z + bD < 0);
		if(fuckthis)
			return false;
		
		//Left Plane
		if(lA * x + lB * y + lC * z + lD < 0)
			return false;
		
		//Right Plane
		if(rA * x + rB * y + rC * z + rD < 0)
			return false;
		
		//Near Plane
		if(nA * x + nB * y + nC * z + nD < 0)
			return false;
		
		//Far Plane
		if(fA * x + fB * y + fC * z + fD < 0)
			return false;
		
		return true;
	}
	
	public ViewFrustrum(){
		
	}
}
