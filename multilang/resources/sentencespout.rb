require "./storm"

class SentenceSpout < Storm::Spout

  def nextTuple
    s = ["a little brown dog",
         "the man petted the dog",
         "four score and seven years ago",
         "an apple a day keeps the doctor away"]
    emit([s.sample])
  end

end

SentenceSpout.new.run
