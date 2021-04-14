package src_girela_castellon_ruben;

import java.util.*;
import core.game.Observation;
import core.game.StateObservation;
import core.logging.Logger;
import core.player.AbstractPlayer;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Utils;
import tracks.ArcadeMachine;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

/**
 * 
 * @author Ruben Girela Castellón
 * Practice 1 (done)
 * - level 0 (simple deliberative agent)
 *
 */

public class myAgent extends AbstractPlayer{
	// 1) Busca la puerta mas cercana. 
	// 2) Escoge la ruta optima a ir a la puerta usando el algoritmo A*.
	Vector2d fescala;
	Vector2d portal;
	
	public Vector<Nodo> path;
	/**
	 * initialize all variables for the agent
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	public myAgent(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//Calculamos el factor de escala entre mundos (pixeles -> grid)
        fescala = new Vector2d(stateObs.getWorldDimension().width / stateObs.getObservationGrid().length , 
        		stateObs.getWorldDimension().height / stateObs.getObservationGrid()[0].length);      
      
        //Se crea una lista de observaciones de portales, ordenada por cercania al avatar
        ArrayList<Observation>[] posiciones = stateObs.getPortalsPositions(stateObs.getAvatarPosition());
        //Seleccionamos el portal mas proximo
        portal = posiciones[0].get(0).position;
        portal.x = Math.floor(portal.x / fescala.x);
        portal.y = Math.floor(portal.y / fescala.y);
        
        //Calcula una ruta optima usando A*
        algoritmoEstrella(stateObs, elapsedTimer);
	}
	
	/**
	 * Apply the actions
	 * @param stateObs Observation of the current state.
     * @param elapsedTimer Timer when the action returned is due.
	 */
	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		//copia por referencia
		Vector2d n = avatar.copy();
		n.set(n.x+2, n.y+2);
		
		//para eliminar el primer elemento y acceder al siguiente elemento
		/*if(path.indexOf(n) == -1) {
		
			path.addElement(avatar);
			path.addElement(n);
			System.out.println("before: " + path.get(0) + ", " + path.get(1));
			
			path.remove(0);
			
			if(path.size()>0) {
				System.out.println("after: " + path.get(0));
				
			}
		}*/
		
		/*if(path.firstElement() == avatar) {
			
		}*/
		/*
		 * UP --> (0, -1)
		 * DOWN --> (0, 1)
		 * RIGHT --> (-1, 0)
		 * LEFT --> (1, 0)
		 */
		//System.out.println("ORIENTACION: " + stateObs.getAvatarOrientation());
		
		
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * Algortimo A* que calcula la ruta optima hacia el portal
	 */
	public void algoritmoEstrella(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		//created the vector of open and closed nodes
		Vector<Nodo> abiertos = new Vector<Nodo>();
		Vector<Nodo> cerrados = new Vector<Nodo>();
		Nodo best_node = new Nodo();
		Integer g = 0;
		
		//end of algorithm stop
		boolean stop = false;
		
		//get avater position
		Vector2d avatar =  new Vector2d(stateObs.getAvatarPosition().x / fescala.x, 
        		stateObs.getAvatarPosition().y / fescala.y);
		
		
		
		//add the first node (the current position of avatar)
		abiertos.add(new Nodo(avatar, avatar, orientacion(stateObs), 0, calculateManhattan(avatar,0)));
		
		do {
			//save the best node
			best_node = bestNode(abiertos, g);
			
			if(best_node.hijo == portal) {//if it is the target node 
				//expand the nodes of the actual node
				expandNodes(abiertos,cerrados,best_node,stateObs, g);
				//and end
				stop = true;
			}else {//else remove the node from the open list
				if(abiertos.indexOf(best_node)>-1)
					abiertos.remove(abiertos.indexOf(best_node));
				
				//and expand the nodes
				expandNodes(abiertos, cerrados, best_node, stateObs, g);
			}
			stop = true;
		}while(!stop);
		
		path = cerrados;
	}
	
	/**
	 * Obtiene el mejor nodo
	 * @param abiertos vector of non-transversed nodes
	 */
	public Nodo bestNode(Vector<Nodo> abiertos, Integer g) {
		//save the best node
		Nodo best_node = new Nodo();
		//save the value h, f and the minimum f
		Integer h=0, f=0, min_f = 9999999;
		
		//traverse all open nodes
		for(Nodo nodo:abiertos) {
			//calculate heuristic distance
			h = calculateManhattan(nodo.hijo,0);
			//and calculate the total distance
			f = g + h;
			//save the node with the smallest distance
			if(f < min_f) {
				f = min_f;
				best_node = nodo;
			}
		}
		//return the best node
		return best_node;
	}
	/**
	 * gets the Manhattan distance
	 * @param current_node current node to calculate distance h 
	 */
	public Integer calculateManhattan(Vector2d current_node, Integer orientacion){
		//Manhattan distance
        Integer distance = 0;
        /*switch(orientacion) {
        case 0://up
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 1://down
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 2://left
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        	break;
        case 3://right
        	distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y)));
        }*/
        
        
        //calculate the Manhattan distance 
        distance = ((int) (Math.abs(current_node.x - portal.x) + Math.abs(current_node.y-portal.y))); 
        
        return distance;
        
	}
	
	
	public Vector<Nodo> expandNodes(Vector<Nodo> abiertos, Vector<Nodo> cerrados, Nodo node, StateObservation stateObs, int g){
		
		//copy unexplored nodes
		Vector <Nodo> copy_abiertos = abiertos;
		//expand node
		Nodo new_node;
		Vector2d hijo;
		ArrayList<Observation> casilla;
		boolean not_wall = false;
		
		int orientacion_avatar = orientacion(stateObs);
		
		//para obtener el tipo de superficie utilizar esto:
		//stateObs.getObservationGrid()[(int)(portal.x)][(int)(portal.y)]);
		
		//[Observation{category=6, itype=9, obsID=99, position=16.0 : 32.0, reference=-1.0 : -1.0, sqDist=1378.0}]
		// esto devuelve un arraylist, pero solo nos interesa category
		
		
		//expand the nodes of the current node
		if (node.hijo.y - 1 >= 0) {//abajo
			//save the new node
			hijo = new Vector2d(node.hijo.x, node.hijo.y-1);
			new_node = new Nodo(node.hijo, hijo, orientacion_avatar,calculateG(g, node.hijo, hijo, orientacion_avatar),calculateManhattan(hijo,0));
			//save the properties of the box
			casilla = stateObs.getObservationGrid()[(int)(new_node.hijo.x)][(int)(new_node.hijo.y)];
			
			//if not empty
			if(casilla.size()>0) {
				//check if it isn't a wall
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			//if the new node hasn't been explored and it isn't a wall
			if(cerrados.indexOf(new_node) == -1 && abiertos.indexOf(new_node) == -1 && not_wall)
				copy_abiertos.add(new_node);//adds it to the list of open nodes
			else if(g < calculateManhattan(new_node.hijo, 0)) {
				
			}else if(abiertos.indexOf(new_node) != -1 && g < calculateManhattan(new_node.hijo, 0)) {
				
			}
        }
		//and repeat for other nodes (up, right and left)
        if (node.hijo.y + 1 <= stateObs.getObservationGrid()[0].length-1) {//arriba
        	
        	hijo = new Vector2d(node.hijo.x, node.hijo.y+1);
        	new_node = new Nodo(node.hijo, hijo, orientacion_avatar, calculateG(g, node.hijo, hijo, orientacion_avatar),calculateManhattan(hijo,0));
			casilla = stateObs.getObservationGrid()[(int)(new_node.hijo.x)][(int)(new_node.hijo.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) == -1 && abiertos.indexOf(new_node) == -1 && not_wall)
				copy_abiertos.add(new_node);
        }
        if (node.hijo.x - 1 >= 0) {//izquierda
        	
        	hijo = new Vector2d(node.hijo.x-1, node.hijo.y);
        	new_node = new Nodo(node.hijo, hijo, orientacion_avatar, calculateG(g, node.hijo, hijo, orientacion_avatar), calculateManhattan(hijo,0));
			casilla = stateObs.getObservationGrid()[(int)(new_node.hijo.x)][(int)(new_node.hijo.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) == -1 && abiertos.indexOf(new_node) == -1 && not_wall)
				copy_abiertos.add(new_node);
        }
        if (node.hijo.x + 1 <= stateObs.getObservationGrid().length - 1) {//derecha
        	
        	hijo = new Vector2d(node.hijo.x+1, node.hijo.y);
        	new_node = new Nodo(node.hijo, hijo, orientacion_avatar, calculateG(g, node.hijo, hijo, orientacion_avatar), calculateManhattan(hijo, 0));
			casilla = stateObs.getObservationGrid()[(int)(new_node.hijo.x)][(int)(new_node.hijo.y)];
			
			if(casilla.size()>0) {
				
				if(casilla.get(0).category != 4)
					not_wall = true;
			}else {
				not_wall = true;
			}
			
			if(cerrados.indexOf(new_node) == -1 && abiertos.indexOf(new_node) == -1 && not_wall)
				copy_abiertos.add(new_node);
        }
		
		return copy_abiertos;
	}
	
	public int orientacion(StateObservation stateObs) {
		/*
		 * UP --> (0, -1) --> 0
		 * DOWN --> (0, 1) --> 1
		 * RIGHT --> (-1, 0) --> 2
		 * LEFT --> (1, 0) --> 3
		 */
		//System.out.println("ORIENTACION: " + stateObs.getAvatarOrientation());
		if(stateObs.getAvatarOrientation().x == 0 && stateObs.getAvatarOrientation().y == -1) 
			return 0;//UP
		else if(stateObs.getAvatarOrientation().x == 0 && stateObs.getAvatarOrientation().y == 1)
			return 1;//DOWN
		else if(stateObs.getAvatarOrientation().x == -1 && stateObs.getAvatarOrientation().y == 0)
			return 2;//RIGHT
		return 3;//LEFT
	}
	
	public int calculateG(int g, Vector2d padre, Vector2d hijo, int orientacion) {
		int new_g = g;
		
		if(padre.x+1 == hijo.x && padre.y == hijo.y){//box right
			if(orientacion == 2)
				++new_g;
			else
				new_g +=2;
		}else if(padre.x-1 == hijo.x && padre.y == hijo.y){//box left
			if(orientacion == 3)
				++new_g;
			else
				new_g +=2;
		}else if(padre.x == hijo.x && padre.y+1 == hijo.y){//box up
			if(orientacion == 0)
				++new_g;
			else
				new_g +=2;
		}else if(padre.x == hijo.x && padre.y-1 == hijo.y){//box down
			if(orientacion == 1)
				++new_g;
			else
				new_g +=2;
		}
		
		
		return new_g;
	}
}
/**
 * class Nodo containing:
 * @param padre: node of parent
 * @param hijo: parent child node
 * @param orientacion: cell orientation relative to avatar orientation
 * @param g: real cost
 * @param h: heuristic cost
 * @author ruben
 *
 */
class Nodo {
	
	public Vector2d padre;
	public Vector2d hijo;
	public int orientacion;
	public int g;
	public int h;
	
	public Nodo() {
		padre = new Vector2d();
		hijo = new Vector2d();
		orientacion = -1;
		h = g = 0;
	}
	
	public Nodo(Vector2d padre, Vector2d hijo, int orientacion, int g, int h) {
		this.padre = padre;
		this.hijo = hijo;
		this.orientacion = orientacion;
		this.g = g;
		this.h = h;
	}
};
