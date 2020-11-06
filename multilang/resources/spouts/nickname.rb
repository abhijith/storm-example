require_relative "../storm"

class NicknameSpout < Storm::Spout

  def nextTuple
    s = [{ id: 1, nickname: "nick-a" },
         { id: 2, nickname: "nick-b" },
         { id: 3, nickname: "nick-c" },
         { id: 4, nickname: "nick-d" }]
    payload = s.sample
    sleep(5)
    emit([payload[:id], payload[:nickname]])
  end

end

NicknameSpout.new.run
