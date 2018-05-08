package de.pantle.qwixx.utils;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.ContactListener;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Daniel on 05.04.2018.
 */

public class Helper {
	
	public static class MyContactListener extends ContactListener {
		@Override
		public boolean onContactAdded(int userValue0, int partId0, int index0, boolean match0, int userValue1, int partId1,
									  int index1, boolean match1) {
			
			return true;
		}
	}
	
	static class MyMotionState extends btMotionState {
		Matrix4 transform;
		
		@Override
		public void getWorldTransform(Matrix4 worldTrans) {
			worldTrans.set(transform);
		}
		
		@Override
		public void setWorldTransform(Matrix4 worldTrans) {
			transform.set(worldTrans);
		}
	}
	
	
	public static class GameObject extends ModelInstance implements Disposable {
		public final btRigidBody body;
		public final MyMotionState motionState;
		
		private final static BoundingBox bounds = new BoundingBox();
		public final Vector3 center = new Vector3();
		public final Vector3 dimensions = new Vector3();
		public final float radius;
		
		public GameObject(Model model, String node, btRigidBody.btRigidBodyConstructionInfo constructionInfo) {
			super(model, node);
			calculateBoundingBox(bounds);
			bounds.getCenter(center);
			bounds.getDimensions(dimensions);
			radius = dimensions.len() / 2f;
			
			motionState = new MyMotionState();
			motionState.transform = transform;
			body = new btRigidBody(constructionInfo);
			body.setMotionState(motionState);
		}
		
		@Override
		public void dispose() {
			body.dispose();
			motionState.dispose();
		}
		
		public static class Constructor implements Disposable {
			public final Model model;
			public final String node;
			public final btCollisionShape shape;
			public final btRigidBody.btRigidBodyConstructionInfo constructionInfo;
			private static Vector3 localInertia = new Vector3();
			
			public Constructor(Model model, String node, btCollisionShape shape, float mass) {
				this.model = model;
				this.node = node;
				this.shape = shape;
				if (mass > 0f)
					shape.calculateLocalInertia(mass, localInertia);
				else
					localInertia.set(0, 0, 0);
				this.constructionInfo = new btRigidBody.btRigidBodyConstructionInfo(mass, null, shape, localInertia);
			}
			
			public GameObject construct() {
				return new GameObject(model, node, constructionInfo);
			}
			
			@Override
			public void dispose() {
				shape.dispose();
				constructionInfo.dispose();
			}
		}
	}
	
}
