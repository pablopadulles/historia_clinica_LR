package net.pladema.establishment.repository.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.pladema.establishment.repository.entity.Room;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RoomVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1053820325768716531L;
	
	private Integer id;
	private String description;
	private String type;
	private String roomNumber;

	
	public RoomVo(Room room) {
		id = room.getId();
		description = room.getDescription();
		type = room.getType();
		roomNumber = room.getRoomNumber();
	}
	
}
