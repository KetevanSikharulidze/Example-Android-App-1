import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.bluetooth.BluetoothDevice

class DeviceAdapter(private val context: Context, private val devices: MutableList<BluetoothDevice>) : BaseAdapter() {

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Any {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, parent, false)

        val device = getItem(position) as BluetoothDevice
        val deviceName = view.findViewById<TextView>(android.R.id.text1)
        val deviceAddress = view.findViewById<TextView>(android.R.id.text2)

        deviceName.text = device.name ?: "Unknown Device"
        deviceAddress.text = device.address

        return view
    }
}
